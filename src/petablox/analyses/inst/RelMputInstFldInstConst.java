package petablox.analyses.inst;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.InstanceFieldRef;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import petablox.analyses.field.DomF;
import petablox.analyses.method.DomM;
import petablox.analyses.var.DomV;
import petablox.analyses.constants.DomConst;
import petablox.program.visitors.IHeapInstVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (m,b,f,x) such that method m
 * contains a quad of the form <tt>b.f = x</tt>, where x is a constant.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "MputInstFldInstConst",
    sign = "M0,V0,F0:F0_M0_V0"
)
public class RelMputInstFldInstConst extends ProgramRel implements IHeapInstVisitor {
    private DomM domM;
    private DomV domV;
    private DomF domF;
    //private DomConst domConst;
    private SootMethod ctnrMethod;
    public void init() {
        domM = (DomM) doms[0];
        domV = (DomV) doms[1];
        domF = (DomF) doms[2];
        //domConst = (DomConst) doms[3];
    }
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        ctnrMethod = m;
    }
    public void visitHeapInst(Unit q) {
        if (q instanceof JAssignStmt) {
        	JAssignStmt j = (JAssignStmt)q;
        	if (SootUtilities.isStoreInst(j)) {
        		Value left = j.leftBox.getValue();
        		Value right = j.rightBox.getValue();
        		if (right instanceof Constant) {
        			JArrayRef jar = (JArrayRef)left;
            		Local b = (Local)jar.getBase();
            		Constant r = (Constant)right;
            		int mIdx = domM.indexOf(ctnrMethod);
                    assert (mIdx >= 0);
                    int bIdx = domV.indexOf(b);
                    assert (bIdx >= 0);
                    //int rIdx = domConst.indexOf(r);
                    //System.out.println(r);
                    //assert (rIdx >= 0);
                    int fIdx = 0;
                    //add(mIdx, bIdx, fIdx, rIdx);
                    add(mIdx, bIdx, fIdx);
        		}
        		return;
        	} else if (SootUtilities.isFieldStore(j)) {
        		SootField f = j.getFieldRef().getField();
        		Value right = j.rightBox.getValue();
        		if (j.leftBox.getValue() instanceof InstanceFieldRef 
        				&& right instanceof Constant) {
        			JInstanceFieldRef jifr = (JInstanceFieldRef)j.leftBox.getValue();
        			Local b = (Local)jifr.getBase();
        			Constant r = (Constant)j.rightBox.getValue();
        			int mIdx = domM.indexOf(ctnrMethod);
                    assert (mIdx >= 0);
                    int bIdx = domV.indexOf(b);
                    assert (bIdx >= 0);
                    //int rIdx = domConst.indexOf(r);
                    //assert (rIdx >= 0);
                    int fIdx = domF.indexOf(f);
                    assert (fIdx >= 0);
                    //add(mIdx, bIdx, fIdx, rIdx);
                    add(mIdx, bIdx, fIdx);
        		}
        	}
        }
    }
}
