package petablox.analyses.syntax;

import petablox.program.visitors.IExprVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ShrExpr;

@Petablox(name = "ShrExpr", sign = "EXPR0,EXPR1,EXPR2:EXPR0_EXPR1xEXPR2")
public class RelShrExpr extends ProgramRel implements IExprVisitor {

	@Override
	public void visit(Unit q) { }

	@Override
	public void visit(SootMethod m) { }

	@Override
	public void visit(SootClass c) { }

	@Override
	public void visit(Value e) {
		if (e instanceof ShrExpr) {
			ShrExpr se = (ShrExpr) e;
			add(e, se.getOp1(), se.getOp2());
		}
	}

}
