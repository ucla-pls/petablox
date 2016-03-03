package petablox.analyses.argret;

import soot.Local;
import soot.SootMethod;
import petablox.analyses.method.DomM;
import petablox.analyses.primitives.DomX;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (m,z,x) such that local variable
 * x is the zth argument variable of method m.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "MmethXArg",
    sign = "M0,Z0,X0:M0_X0_Z0"
)
public class RelMmethXArg extends ProgramRel {
    @Override
    public void fill() {
        DomM domM = (DomM) doms[0];
        DomX domX = (DomX) doms[2];
        int numM = domM.size();
        for (int mIdx = 0; mIdx < numM; mIdx++) {
            SootMethod m = domM.get(mIdx);
            if (!m.isConcrete())
                continue;
            Local[] rf = SootUtilities.getMethArgLocals(m);
            int numArgs = rf.length;
            for (int zIdx = 0; zIdx < numArgs; zIdx++) {
                Local x=rf[zIdx];
                int xIdx = domX.indexOf(x);
                assert (xIdx >= 0);
                add(mIdx, zIdx, xIdx);
            }
        }
    }
}
