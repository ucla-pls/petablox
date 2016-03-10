package petablox.analyses.inst;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import petablox.analyses.field.DomF;
import petablox.analyses.method.DomM;
import petablox.analyses.var.DomV;
import petablox.analyses.primitives.DomX;
import petablox.program.visitors.IHeapInstVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (m,x,b,f) such that method m
 * contains a quad of the form <tt>x = b.f</tt>.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "MgetInstFldInstX",
    sign = "M0,X0,V0,F0:F0_X0_M0_V0"
)
public class RelMgetInstFldInstX extends ProgramRel implements IHeapInstVisitor {
    private DomM domM;
    private DomX domX;
    private DomV domV;
    private DomF domF;
    public void init() {
        domM = (DomM) doms[0];
        domX = (DomX) doms[1];
        domV = (DomV) doms[2];
        domF = (DomF) doms[3];
    }
    private SootMethod ctnrMethod;
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        ctnrMethod = m;
    }
    public void visitHeapInst(Unit q) {
        if (q instanceof JAssignStmt) {
        	JAssignStmt j = (JAssignStmt)q;
        	if (SootUtilities.isLoadInst(j)) {
    			Local l = (Local)j.leftBox.getValue();
    			Local b = (Local)((JArrayRef)j.rightBox.getValue()).getBase();
    			int mIdx = domM.indexOf(ctnrMethod);
                assert (mIdx >= 0);
                int lIdx = domX.indexOf(l);
                assert (lIdx >= 0);
                int bIdx = domV.indexOf(b);
                assert (bIdx >= 0);
                int fIdx = 0;
                add(mIdx, lIdx, bIdx, fIdx);
        	} else if (SootUtilities.isFieldLoad(j)) {
        		if (j.rightBox.getValue() instanceof JInstanceFieldRef) {
        			JInstanceFieldRef jifr = (JInstanceFieldRef)j.rightBox.getValue();
        			Local l = (Local)j.leftBox.getValue();
        			Local b = (Local)jifr.getBase();
        			SootField f = j.getFieldRef().getField();
        			int mIdx = domM.indexOf(ctnrMethod);
                    assert (mIdx >= 0);
                    int bIdx = domV.indexOf(b);
                    assert (bIdx >= 0);
                    int lIdx = domX.indexOf(l);
                    assert (lIdx >= 0);
                    int fIdx = domF.indexOf(f);
                    assert (fIdx >= 0);
                    add(mIdx, lIdx, bIdx, fIdx);
        		}
        	}
        }
    }
}
