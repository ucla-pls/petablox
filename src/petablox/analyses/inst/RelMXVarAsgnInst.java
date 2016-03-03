package petablox.analyses.inst;

import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
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

/**
 * Relation containing each tuple (m,x1,x2) such that method m contains a
 * statement of the form x1 = x2 where x1 and x2 are of any type.
 * 
 * Includes three kinds of quads: MOVE, CHECKCAST, PHI.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(name = "MXVarAsgnInst", sign = "M0,X0,X1:M0_X0xX1")
public class RelMXVarAsgnInst extends ProgramRel implements IMoveInstVisitor, IPhiInstVisitor, ICastInstVisitor {
    private SootMethod ctnrMethod;
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        ctnrMethod = m;
    }
    public void visitMoveInst(Unit q) {
        if (q instanceof JAssignStmt) {
        	JAssignStmt j = (JAssignStmt) q;
        	if (SootUtilities.isMoveInst(j)) {
    			Local r = (Local) j.rightBox.getValue();
    			Local l = (Local) j.leftBox.getValue();
    			add(ctnrMethod, l, r);
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
    				if (v instanceof Local) {
    					add(ctnrMethod, l, (Local) v);
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
    			if (rv instanceof Local) {
    				Local r = (Local) rv;
    				add(ctnrMethod, l, r);
    			}
    		}
    	}
    }
}
