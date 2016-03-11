package petablox.analyses.argret;

import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JReturnStmt;
import petablox.program.visitors.IReturnInstVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing each tuple (m,z,x) such that local variable
 * x is the zth return variable of method m.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "MmethXRet",
    sign = "M0,Z0,X0:M0_X0_Z0"
)
public class RelMmethXRet extends ProgramRel implements IReturnInstVisitor {
    private static Integer ZERO = new Integer(0);
    private SootMethod ctnrMethod;

    @Override
    public void visit(SootClass c) { }

    @Override
    public void visit(SootMethod m) {
        ctnrMethod = m;
    }

    @Override
    public void visitReturnInst(Unit u) {
    	if (u instanceof JReturnStmt) {
    		JReturnStmt rs = (JReturnStmt) u;
    		Value op = rs.getOp();
    		// note: op is null if this method returns void
    		if (op instanceof Local){
				add(ctnrMethod, ZERO, op);
    		}
    	}      
    }
}
