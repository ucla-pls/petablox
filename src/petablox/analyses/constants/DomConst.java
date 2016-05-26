package petablox.analyses.constants;

import java.util.Iterator;

import soot.Body;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Constant;
import soot.jimple.StringConstant;
import soot.jimple.internal.ImmediateBox;
import petablox.analyses.method.DomM;
import petablox.project.Petablox;
import petablox.project.ClassicProject;
import petablox.project.Config;
import petablox.project.analyses.ProgramDom;

/**
 * Domain of numeric, boolean, string, and class constants.
 *
 * @author Joe Cox (cox@cs.ucla.edu)
 */
 
@Petablox(name = "Const", consumes = { "M" })

public class DomConst extends ProgramDom<Constant> {
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
            Body b = m.getActiveBody();
            for (ValueBox vb : b.getUseAndDefBoxes()) {
                if (vb instanceof ImmediateBox) {
                    Value v = vb.getValue();
                    if (v instanceof Constant)
                        add((Constant) v);
                }
            }
        }
    }
}
