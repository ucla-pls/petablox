package petablox.analyses.method;

import soot.SootClass;
import soot.SootMethod;
import petablox.program.visitors.IMethodVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing all native methods.
 *
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "arraycopyM",
    sign = "M0"
)
public class RelArrayCopyM extends ProgramRel implements IMethodVisitor {
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        if (m.getSignature().equals("<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>")) {
            add(m);
        }
    }
}
