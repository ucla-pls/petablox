package petablox.analyses.inst;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.internal.JAssignStmt;
import petablox.analyses.field.DomF;
import petablox.analyses.method.DomM;
import petablox.analyses.primitives.DomX;
import petablox.program.visitors.IHeapInstVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (m,x,f) such that method m contains
 * a quad of the form <tt>x = f</tt>.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "MgetStatFldInstX",
    sign = "M0,X0,F0:F0_M0_X0"
)
public class RelMgetStatFldInstX extends ProgramRel implements IHeapInstVisitor {
    private DomM domM;
    private DomX domX;
    private DomF domF;
    private SootMethod ctnrMethod;
    public void init() {
        domM = (DomM) doms[0];
        domX = (DomX) doms[1];
        domF = (DomF) doms[2];
    }
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        ctnrMethod = m;
    }
    public void visitHeapInst(Unit q) {
    	if (q instanceof JAssignStmt) {
    		JAssignStmt j = (JAssignStmt)q;
    		if (SootUtilities.isStaticGet(j)) {
    			SootField f = j.getFieldRef().getField();
    			Local l = (Local)j.leftBox.getValue();
    			int mIdx = domM.indexOf(ctnrMethod);
                assert (mIdx >= 0);
                int lIdx = domX.indexOf(l);
                assert (lIdx >= 0);
                int fIdx = domF.indexOf(f);
                assert (fIdx >= 0);
                add(mIdx, lIdx, fIdx);
    		}
    	}
    }
}
