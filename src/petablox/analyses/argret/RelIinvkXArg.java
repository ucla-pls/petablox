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
 * Relation containing each tuple (i,z,x) such that local variable x
 * is the zth argument variable of method invocation quad i.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "IinvkXArg",
    sign = "I0,Z0,X0:I0_X0_Z0"
)
public class RelIinvkXArg extends ProgramRel {
    @Override
    public void fill() {
        DomI domI = (DomI) doms[0];
        DomX domX = (DomX) doms[2];
        int numI = domI.size();
        for (int iIdx = 0; iIdx < numI; iIdx++) {
            Unit u = (Unit) domI.get(iIdx);
            List<Value> l = SootUtilities.getInvokeArgs(u);
            if(SootUtilities.isInstanceInvoke(u)){
	            Value thisV = SootUtilities.getInstanceInvkBase(u);
	            l.add(0, thisV);
            }
            int numArgs = l.size();
            for (int zIdx = 0; zIdx < numArgs; zIdx++) {
                Value x=l.get(zIdx);
                if (x instanceof Local) {
                	Local r = (Local)x;
                    int xIdx = domX.indexOf(r);
                    assert (xIdx >= 0);
                    add(iIdx, zIdx, xIdx);
                }
            }
        }
    }
}
