package petablox.analyses.basicblock;

import java.util.List;

import soot.toolkits.graph.Block;
import petablox.analyses.basicblock.DomB;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing each pair of basic blocks (b1,b2)
 * such that b2 is immediate successor of b1. 
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(
    name = "succBB",
    sign = "B0,B1:B0xB1"
)
public class RelSuccBB extends ProgramRel {
    public void fill() {
        DomB domB = (DomB) doms[0];
        int numB = domB.size();
        for (int bIdx = 0; bIdx < numB; bIdx++) {
            Block bb = domB.get(bIdx);
            List<Block> succs = bb.getSuccs();
            for (Block bb2 : succs) {
                int bIdx2 = domB.indexOf(bb2);
                assert (bIdx2 >= 0);
                add(bIdx, bIdx2);
            }
        }
    }
}
