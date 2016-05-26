package petablox.analyses.argret;

import java.util.List;

import soot.Unit;
import soot.Local;
import soot.Value;
import soot.jimple.Constant;
import petablox.analyses.invk.DomI;
import petablox.analyses.constants.DomConst;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (i,z,x) such that constant x
 * is the zth argument variable of method invocation quad i.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "IinvkConstArg",
    sign = "I0,Z0:I0_Z0"
)
public class RelIinvkConstArg extends ProgramRel {
    @Override
    public void fill() {
        DomI domI = (DomI) doms[0];
        // DomConst domConst = (DomConst) doms[2];
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
                if (x instanceof Constant) {
                	Constant r = (Constant)x;
                    //int xIdx = domConst.indexOf(r);
                    //assert (xIdx >= 0);
                    add(iIdx, zIdx);
                }
            }
        }
    }
}
