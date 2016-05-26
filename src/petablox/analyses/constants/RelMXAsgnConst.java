package petablox.analyses.constants;

import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.shimple.internal.SPhiExpr;
import soot.toolkits.scalar.ValueUnitPair;

import java.util.List;

import petablox.program.visitors.ICastInstVisitor;
import petablox.program.visitors.IMoveInstVisitor;
import petablox.program.visitors.IPhiInstVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.SootUtilities;

@Petablox(
    name = "MXAsgnConst",
    sign = "M0,X0:M0_X0"
)
public class RelMXAsgnConst extends ProgramRel implements IMoveInstVisitor, IPhiInstVisitor, ICastInstVisitor {
    private SootMethod ctnrMethod;
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        ctnrMethod = m;
    }

    public void visitMoveInst(Unit q) {
        if (q instanceof JAssignStmt) {
        	JAssignStmt j = (JAssignStmt) q;
            Value l = j.leftBox.getValue();
            Value r = j.rightBox.getValue();
        	if (l instanceof Local && r instanceof Constant) {
    			add(ctnrMethod, (Local) l);
        	}
        }
    }
    public void visitPhiInst(Unit q) {
    	if (q instanceof JAssignStmt) {
    		JAssignStmt j = (JAssignStmt) q;
    		Value left = j.leftBox.getValue();
    		Value right = j.rightBox.getValue();
    		if (right instanceof SPhiExpr) {
    			SPhiExpr phi = (SPhiExpr) right;
    			Local l = (Local) left;
    			List<ValueUnitPair> args = phi.getArgs();
    			for (ValueUnitPair vu : args) {
    				Value v = vu.getValue();
    				if (v instanceof Constant) {
    					add(ctnrMethod, l);
    				}
    			}
    		}
    	}
    }
    public void visitCastInst(Unit q) {
    	if (q instanceof JAssignStmt) {
    		JAssignStmt j = (JAssignStmt) q;
    		Value left = j.leftBox.getValue();
    		Value right = j.rightBox.getValue();
    		if (right instanceof JCastExpr) {
    			Local l = (Local) left;
    			Value rv = ((JCastExpr) right).getOp();
    			if (rv instanceof Constant) {
    				add(ctnrMethod, l);
    			}
    		}
    	}
    }
}
