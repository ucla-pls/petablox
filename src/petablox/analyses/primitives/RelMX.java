package petablox.analyses.primitives;

import soot.SootMethod;
import soot.Local;
import petablox.analyses.primitives.DomX;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing each tuple (m,x) such that method m
 * declares local variable x, that is, x is either an
 * argument or temporary variable of m.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "MX",
    sign = "M0,X0:M0_X0"
)
public class RelMX extends ProgramRel {
    public void fill() {
        DomX domX = (DomX) doms[1];
        for (Local x : domX) {
            SootMethod m = domX.getMethod(x);
            add(m, x);
        }
    }
}
