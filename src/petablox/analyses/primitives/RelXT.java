package petablox.analyses.primitives;

import java.util.Iterator;

import petablox.program.Program;
import petablox.program.visitors.IMethodVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;
import soot.Local;
import soot.RefLikeType;
import soot.PrimType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

/**
 * Relation containing each tuple (x,t) such that local variable x of primitive type has type t.
 * If SSA is used (system property {@code petablox.ssa} is set to true) then it is guaranteed that
 * each local variable x has a unique type t.
 * 
 * @author Joe Cox (cox@cs.ucla.edu)
 */

@Petablox(
    name = "XT",
    sign = "X0,T0:T0_X0"
)
public class RelXT extends ProgramRel implements IMethodVisitor {
    private RefLikeType javaLangObject;
    public void init() {
        javaLangObject = Program.g().getClass("java.lang.Object");
        assert (javaLangObject != null);
    }
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        if (!m.isConcrete())
            return;
        Iterator<Local> itr = SootUtilities.getLocals(m).iterator();
        while (itr.hasNext()) {
        	Local l = itr.next();
        	Type t = l.getType();
    		add(l,t);
        }
    }
}
