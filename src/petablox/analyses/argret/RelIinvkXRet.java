package petablox.analyses.argret;

import soot.Unit;
import soot.jimple.internal.JAssignStmt;
import soot.Local;
import soot.RefLikeType;
import petablox.analyses.invk.DomI;
import petablox.analyses.primitives.DomX;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing each tuple (i,z,x) such that local variable x
 * is the zth return variable of method invocation quad i.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "IinvkXRet",
    sign = "I0,Z0,X0:I0_X0_Z0"
)
public class RelIinvkXRet extends ProgramRel {
    @Override
    public void fill() {
        DomI domI = (DomI) doms[0];
        DomX domX = (DomX) doms[2];
        int numI = domI.size();
        for (int iIdx = 0; iIdx < numI; iIdx++) {
            Unit u = (Unit) domI.get(iIdx);           
            Local x = null;
            if (u instanceof JAssignStmt) x = (Local)((JAssignStmt)u).leftBox.getValue();
            if (x != null) {
                int xIdx = domX.indexOf(x);
                assert (xIdx >= 0);
                add(iIdx, 0, xIdx);
            }
        }
    }
}
