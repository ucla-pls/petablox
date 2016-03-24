package petablox.analyses.literals;

import java.util.Iterator;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.internal.JAssignStmt;
import soot.toolkits.graph.Block;
import petablox.analyses.method.DomM;
import petablox.project.Petablox;
import petablox.project.ClassicProject;
import petablox.project.Config;
import petablox.project.analyses.ProgramDom;
import petablox.util.soot.ICFG;
import petablox.util.soot.SootUtilities;

/**
 * Domain of numeric, boolean, string, and class literals.
 *
 * @author Joe Cox (cox@cs.ucla.edu)
 */
 
@Petablox(name = "Lit", consumes = { "M" })

public class DomLit extends ProgramDom<Value> {
    protected DomM domM;
    
    @Override
    public void init() {
        domM = (DomM) (Config.classic ? ClassicProject.g().getTrgt("M") : consumes[0]);
    }
    
    @Override
    public void fill() {
        int numM = domM.size();
        for (int mIdx = 0; mIdx < numM; mIdx++) {
            SootMethod m = domM.get(mIdx);
            if (!m.isConcrete())
                continue;
            ICFG cfg = SootUtilities.getCFG(m);
            for (Block bb : cfg.reversePostOrder()) {
            	Iterator<Unit> uit = bb.iterator();
            	while (uit.hasNext()) {
            		Unit u = uit.next();
            		if (u instanceof JAssignStmt) {
                    	JAssignStmt as = (JAssignStmt) u;
                        Value r = as.rightBox.getValue();
                    	if (r instanceof Constant) 
                            add(r);
            		}	
            	}    
            }
        }
    }
}
