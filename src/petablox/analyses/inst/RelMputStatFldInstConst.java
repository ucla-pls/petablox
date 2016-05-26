package petablox.analyses.inst;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Constant;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JAssignStmt;
import petablox.analyses.field.DomF;
import petablox.analyses.method.DomM;
import petablox.analyses.constants.DomConst;
import petablox.program.visitors.IHeapInstVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (m,f,x) such that method m contains
 * a quad of the form <tt>f = x</tt>, where x is a constant.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "MputStatFldInstConst",
    sign = "M0,F0:F0_M0"
)
public class RelMputStatFldInstConst extends ProgramRel implements IHeapInstVisitor {
    private DomM domM;
    private DomF domF;
    //private DomConst domConst;
    private SootMethod ctnrMethod;
    public void init() {
        domM = (DomM) doms[0];
        domF = (DomF) doms[1];
        //domConst = (DomConst) doms[2];
    }
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        ctnrMethod = m;
    }
    public void visitHeapInst(Unit q) {
        if(q instanceof JAssignStmt){
        	JAssignStmt j = (JAssignStmt)q;
        	if(SootUtilities.isStaticPut(j)){
        		SootField f = j.getFieldRef().getField();
    			if (j.rightBox.getValue() instanceof Constant) {
        			Constant r = (Constant)j.rightBox.getValue();
        			int mIdx = domM.indexOf(ctnrMethod);
                    assert (mIdx >= 0);
                    //int rIdx = domConst.indexOf(r);
                    //assert (rIdx >= 0);
                    int fIdx = domF.indexOf(f);
                    assert (fIdx >= 0);
                    //add(mIdx, fIdx, rIdx);
                    add(mIdx,fIdx);
    			}
        	}
        }
    }
}
