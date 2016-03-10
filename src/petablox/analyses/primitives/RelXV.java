package petablox.analyses.primitives;

import soot.Local;
import petablox.analyses.primitives.DomX;
import petablox.analyses.var.DomV;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing each tuple (x,v) such that local variable v
 * from domain V is the same local variable as x from domain X.
 *
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "XV",
    sign = "X0,V0:X0_V0"
)
public class RelXV extends ProgramRel {
    public void fill() {
        DomX domX = (DomX) doms[0];
        DomV domV = (DomV) doms[1];
        for (Local v : domV) {
            Local x = domX.getLocalFromUniqueStr(domV.toUniqueString(v));
            add(x, v);
        }
    }
}
