package petablox.analyses.heapacc;

import petablox.analyses.heapacc.DomE;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;
import soot.Unit;
import soot.jimple.internal.JAssignStmt;

/**
 * Relation containing each quad that accesses (reads or writes) a static field
 * (as opposed to an instance field or array element).
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(
    name = "statFldE",
    sign = "E0"
)
public class RelStatFldE extends ProgramRel {
    public void fill() {
        DomE domE = (DomE) doms[0];
        int numE = domE.size();
        for (int eIdx = 0; eIdx < numE; eIdx++) {
            Unit e = (Unit) domE.get(eIdx);
            //Operator op = e.getOperator();
            if(e instanceof JAssignStmt){
            	JAssignStmt j = (JAssignStmt)e;
            	if (SootUtilities.isStaticGet(j) || SootUtilities.isStaticPut(j)) {
                    add(eIdx);
                }
            }
            
        }
    }
}
