package petablox.analyses.argret;

import java.util.List;

import soot.Unit;
import soot.Local;
import soot.Value;
import petablox.analyses.invk.DomI;
import petablox.analyses.primitives.DomX;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (i,x) such that local variable x
 * is the 0th argument variable of method invocation quad i.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "IinvkXArg0",
    sign = "I0,X0:I0_X0"
)
public class RelIinvkXArg0 extends ProgramRel {
    @Override
    public void fill() {
        DomI domI = (DomI) doms[0];
        DomX domX = (DomX) doms[1];
        int numI = domI.size();
        for (int iIdx = 0; iIdx < numI; iIdx++) {
            Unit u = (Unit) domI.get(iIdx);
            Value x = null;
            if (SootUtilities.isInstanceInvoke(u)) {
            	x = SootUtilities.getInstanceInvkBase(u);
            } else if (SootUtilities.isStaticInvoke(u)) {
	            List<Value> l = SootUtilities.getInvokeArgs(u);
	            if (l.size() > 0) {
	            	x = l.get(0);
	            }
            }
            if (x != null && x instanceof Local) {
            	Local r = (Local) x;
                int xIdx = domX.indexOf(r);
                assert (xIdx >= 0);
                add(iIdx, xIdx);
            }
        }
    }
}
