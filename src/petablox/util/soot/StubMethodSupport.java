package petablox.util.soot;

import java.util.HashMap;
import java.util.List;

import petablox.project.Config;
import soot.ArrayType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JAssignStmt;
import soot.util.Chain;

public class StubMethodSupport {
	  public static HashMap <SootMethod,SootMethod> methodToStub = new HashMap<SootMethod,SootMethod>();

    private static boolean isExcluded(SootMethod m) {
    	SootClass c = m.getDeclaringClass();
    	if (Config.isExcludedFromScope(c.getName()))
    		return true;
    	return false;
    }

  	public static SootMethod getStub(SootMethod m) {
        if (methodToStub.containsKey(m))
    		    return methodToStub.get(m);
    	  else 
            return replace(m);
    }

    private static SootMethod replace(SootMethod m) {
        String cls = m.getDeclaringClass().getName();
        String name = m.getName();
        String rtype = m.getReturnType().toString();
        List<Type> ptypes = m.getParameterTypes();
        /* Do not use m.getSignature() for the comparison. It is costly. */
        if (cls.equals("java.lang.Thread") && name.equals("start") &&
            rtype.equals("void") && ptypes.size() == 0)
            return getThreadStartEquiv(m);
        else if (cls.equals("java.lang.Object") && name.equals("clone") &&
            rtype.equals("java.lang.Object") && ptypes.size() == 0)
            return getCloneEquiv(m);
		    else if (cls.equals("java.lang.System") && name.equals("arraycopy") &&
            rtype.equals("void") &&
            ptypes.toString().equals("[java.lang.Object, int, java.lang.Object, int, int]"))
            return getArrayCopyEquiv(m);
        else if (cls.equals("java.lang.reflect.Array") && name.equals("set") &&
            rtype.equals("void") && ptypes.toString().equals("[java.lang.Object, int, java.lang.Object]"))
		        return getArraySetEquiv(m);
        else if (cls.equals("java.security.AccessController") && name.equals("doPrivileged") &&
            rtype.equals("java.lang.Object")) {
            String params = ptypes.toString();
            if (params.equals("[java.security.PrivilegedAction]")) 
                return getDoPrivileged(m);
            else if (params.equals("[java.security.PrivilegedExceptionAction]")) 
                return getDoPrivileged(m);
		        else if (params.equals("[java.security.PrivilegedAction, java.security.AccessControlContext]")) 
                return getDoPrivileged(m);
    		    else if (params.equals("[java.security.PrivilegedExceptionAction, java.security.AccessControlContext]")) 
                return getDoPrivileged(m);
            else return emptyStub(m);
        } else if (m.isNative() || isExcluded(m)) return emptyStub(m);
        else return null;
	  }
	
	  private static SootMethod genericMethod(SootMethod m, boolean removeSync) {
    		SootClass c = m.getDeclaringClass();
    		SootMethod s = new SootMethod(m.getName(), m.getParameterTypes(), m.getReturnType(), m.getModifiers() & ~Modifier.NATIVE);
    		if (removeSync) s.setModifiers(s.getModifiers() & ~Modifier.SYNCHRONIZED);
    		c.removeMethod(m);
    		c.addMethod(s);
    		if (s.isConcrete()) {
      			JimpleBody body = Jimple.v().newBody(s);
      			Chain<Unit> units = body.getUnits();
      			Chain<Local> locals = body.getLocals();
      			boolean isStatic = (s.getModifiers() & Modifier.STATIC) != 0;
      			if (!isStatic) {
        				Local thisLcl = Jimple.v().newLocal("this", c.getType());
    		        locals.add(thisLcl);
    		        units.add(Jimple.v().newIdentityStmt(thisLcl, Jimple.v().newThisRef(c.getType())));
      			}
      			int paramCnt = s.getParameterCount();
      			List<Type> paramTypeList = s.getParameterTypes();
      			for (int i = 0; i < paramCnt; i++) {
        				Local lcl = Jimple.v().newLocal("l" + i, paramTypeList.get(i));
    		        locals.add(lcl);
    		        units.add(Jimple.v().newIdentityStmt(lcl, Jimple.v().newParameterRef(paramTypeList.get(i), i)));
      			}
      			s.setActiveBody(body);
  	  	}
    		return s;
  	}
	
	/**
	 * Stub for instance method "void start()" in class java.lang.Thread.
	 */	
	private static SootMethod getThreadStartEquiv(SootMethod m) {
		SootMethod s = genericMethod(m, true);
		JimpleBody body =(JimpleBody) s.retrieveActiveBody();
		SootClass c = s.getDeclaringClass();
		String runSig = "void run()";
		SootMethod runM = c.getMethod(runSig);
		Chain<Unit> units = body.getUnits();
		Local thisLcl = body.getThisLocal();
		units.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(thisLcl, runM.makeRef())));
		units.add(Jimple.v().newReturnVoidStmt());
		methodToStub.put(m, s);
		methodToStub.put(s, s);
		if (Config.verbose >= 2)
			System.out.println("Custom stub (getThreadStartEquiv) for method: " + s.getName() + ":" + s.getDeclaringClass());
		return s;
	}
	
	/**
	 * Stub for instance method "java.lang.Object clone()" in class java.lang.Object.
	 */
	private static SootMethod getCloneEquiv(SootMethod m) {
		SootMethod s = genericMethod(m, false);
		JimpleBody body =(JimpleBody) s.retrieveActiveBody();
		SootClass c = s.getDeclaringClass();
		Chain<Unit> units = body.getUnits();
		Chain<Local> locals = body.getLocals();
        Local lcl1 = Jimple.v().newLocal("l", c.getType());
        locals.add(lcl1);
		Local thisLocal = body.getThisLocal();
		units.add(Jimple.v().newAssignStmt(lcl1,thisLocal));
		units.add(Jimple.v().newReturnStmt(lcl1));
		methodToStub.put(m, s);
		methodToStub.put(s, s);
		if (Config.verbose >= 2)
			System.out.println("Custom stub (getCloneEquiv) for method: " + s.getName() + ":" + s.getDeclaringClass());
		return s;
	}
	
	/**
	 * Stub for static method "void arraycopy(Object src, int srcPos, Object dest,
	 * int destPos, int length)" in class java.lang.System.
	 * Stub code is as follows:
	 * t0 = (Object[])param0
	 * t1 = (Object[])param2
	 * t2 = t0[0]
	 * t1[0] = t2
	 * return
	 */
	private static SootMethod getArrayCopyEquiv(SootMethod m) {
		SootMethod s = genericMethod(m, false);
		JimpleBody body =(JimpleBody) s.retrieveActiveBody();
		SootClass c = s.getDeclaringClass();
		Chain<Unit> units = body.getUnits();
		Chain<Local> locals = body.getLocals();
		
		//t0 = (Object[])param0
		Local l0 = body.getParameterLocal(0);
		CastExpr ce0 = Jimple.v().newCastExpr(l0, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local t0 = Jimple.v().newLocal("t0", ArrayType.v(RefType.v("java.lang.Object"), 1));
        locals.add(t0);
        units.add(Jimple.v().newAssignStmt(t0, ce0));
        
        //t1 = (Object[])param2
        Local l2 = body.getParameterLocal(2);
		CastExpr ce2 = Jimple.v().newCastExpr(l2, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local t1 = Jimple.v().newLocal("t1", ArrayType.v(RefType.v("java.lang.Object"), 1));
        locals.add(t1);
        units.add(Jimple.v().newAssignStmt(t1, ce2));
        
        //t2 = t0[0]
        Local t2 = Jimple.v().newLocal("t2", ArrayType.v(RefType.v("java.lang.Object"), 1));
        locals.add(t2);
        ArrayRef ar1 = Jimple.v().newArrayRef((Value)t0, (Value)IntConstant.v(0));
        units.add(Jimple.v().newAssignStmt(t2, ar1));
        
        //t1[0] = t2
        ArrayRef ar2 = Jimple.v().newArrayRef((Value)t1, (Value)IntConstant.v(0));
        units.add(Jimple.v().newAssignStmt(ar2, t2));
        
        //return
		units.add(Jimple.v().newReturnVoidStmt());
		
		methodToStub.put(m, s);
		methodToStub.put(s, s);
		if (Config.verbose >= 2)
			System.out.println("Custom stub (getArrayCopyEquiv) for method: " + s.getName() + ":" + s.getDeclaringClass());
		return s;
	}
	
	/**
	 * Stub for static method "void set(Object array, int index, Object value)
	 * in class java.lang.reflect.Array.
	 * Stub code is as follows:
	 * t0 = (Object[])param0
	 * t0[0] = param2
	 * return
	 */
	private static SootMethod getArraySetEquiv(SootMethod m) {
		SootMethod s = genericMethod(m, false);
		JimpleBody body =(JimpleBody) s.retrieveActiveBody();
		SootClass c = s.getDeclaringClass();
		Chain<Unit> units = body.getUnits();
		Chain<Local> locals = body.getLocals();
		
		//t0 = (Object[])param0
		Local l0 = body.getParameterLocal(0);
		CastExpr ce0 = Jimple.v().newCastExpr(l0, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local t0 = Jimple.v().newLocal("t0", c.getType());
        locals.add(t0);
        units.add(Jimple.v().newAssignStmt(t0, ce0));
         
        //t0[0] = param2
        Local l2 = body.getParameterLocal(2);
        ArrayRef ar2 = Jimple.v().newArrayRef((Value)t0, (Value)IntConstant.v(0));
        units.add(Jimple.v().newAssignStmt(ar2, l2));
        
        //return
		units.add(Jimple.v().newReturnVoidStmt());
		
		methodToStub.put(m, s);
		methodToStub.put(s, s);
		if (Config.verbose >= 2)
			System.out.println("Custom stub (getArraySetEquiv) for method: " + s.getName() + ":" + s.getDeclaringClass());
		return s;
	}
	
	/* 
	 * Stub for method: java.lang.Object doPrivileged(java.security.PrivilegedAction)
	 * in class java.security.AccessController
	 * Stub code:
	 * t0 = this.run()
	 * return t0
	 */
	private static SootMethod getDoPrivileged(SootMethod m){
		SootMethod s = genericMethod(m,false);
		JimpleBody body =(JimpleBody) s.retrieveActiveBody();
		List<Type> paramTypes = m.getParameterTypes();
		RefType param1 = (RefType)paramTypes.get(0);
		SootClass c = param1.getSootClass();
		String runSig = "java.lang.Object run()";
		SootMethod runM = c.getMethod(runSig);
		Chain<Unit> units = body.getUnits();
		Chain<Local> locals = body.getLocals();
		Local invokeBase = locals.getFirst();
		Local t0 = Jimple.v().newLocal("t0", RefType.v("java.lang.Object"));
		locals.add(t0);
		units.add(Jimple.v().newAssignStmt(t0, Jimple.v().newInterfaceInvokeExpr(invokeBase, runM.makeRef())));
		units.add(Jimple.v().newReturnStmt(t0));
		methodToStub.put(m, s);
		methodToStub.put(s, s);
		if (Config.verbose >= 2)
			System.out.println("Custom stub (getDoPrivileged1) for method: " + s.getName() + ":" + s.getDeclaringClass());
		return s;
	}
	
  	/**
	   * Empty stub for unsupported native methods / excluded methods
  	 */
  	private static SootMethod emptyStub(SootMethod m) {
        SootMethod s = genericMethod(m, false);
        if (s.isConcrete()) {
      			JimpleBody body =(JimpleBody) s.retrieveActiveBody();
      			SootClass c = s.getDeclaringClass();
      			Chain<Unit> units = body.getUnits();
      			if (s.getReturnType() instanceof VoidType)
        				units.add(Jimple.v().newReturnVoidStmt());
      			else {
        				Chain<Local> locals = body.getLocals();
        				Local lcl = Jimple.v().newLocal("retlcl", s.getReturnType());
    		        locals.add(lcl);
    		        units.add(Jimple.v().newReturnStmt(lcl));
      			}
        }
    		methodToStub.put(m, s);
		    methodToStub.put(s, s);
    		if (Config.verbose >= 2)
      			System.out.println("Empty stub for method: " + s.getName() + ":" + s.getDeclaringClass());
    		return s;
	  }
}
