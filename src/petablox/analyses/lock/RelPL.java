package petablox.analyses.lock;

import soot.Unit;
import petablox.analyses.lock.DomL;
import petablox.analyses.point.DomP;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing each tuple (p,e) such that the quad at
 * program point p is a heap-accessing quad e that accesses
 * (reads or writes) an instance field, a static field, or an
 * array element.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(
    name = "PL",
    sign = "P0,L0:P0xL0"
)
public class RelPL extends ProgramRel {
    public void fill() {
        DomP domP = (DomP) doms[0];
        DomL domL = (DomL) doms[1];
        int numL = domL.size();
        for (int lIdx = 0; lIdx < numL; lIdx++) {
            Unit u = domL.get(lIdx);
            int pIdx = domP.indexOf(u);
            assert (pIdx >= 0);
            add(pIdx, lIdx);
        }
    }
}
