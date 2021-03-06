package petablox.analyses.lock;

import soot.SootMethod;
import soot.Unit;
import petablox.analyses.lock.DomL;
import petablox.analyses.method.DomM;
import petablox.program.Program;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (m,l) such that method m contains
 * lock acquisition point l.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(
    name = "ML",
    sign = "M0,L0:M0_L0"
)
public class RelML extends ProgramRel {
    public void fill() {
        DomM domM = (DomM) doms[0];
        DomL domL = (DomL) doms[1];
        int numL = domL.size();
        for (int lIdx = 0; lIdx < numL; lIdx++) {
            Unit u = domL.get(lIdx);
            SootMethod m = SootUtilities.getMethod(u);
            int mIdx = domM.indexOf(m);
            assert (mIdx >= 0);
            add(mIdx, lIdx);
        }
    }
}
