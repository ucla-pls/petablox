package petablox.analyses.syntax;

import petablox.program.visitors.IExprVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.NeExpr;

@Petablox(name = "NeExpr", sign = "EXPR0,EXPR1,EXPR2:EXPR0_EXPR1xEXPR2")
public class RelNeExpr extends ProgramRel implements IExprVisitor {

	@Override
	public void visit(Unit q) { }

	@Override
	public void visit(SootMethod m) { }

	@Override
	public void visit(SootClass c) { }

	@Override
	public void visit(Value e) {
		if (e instanceof NeExpr) {
			NeExpr ne = (NeExpr) e;
			add(e, ne.getOp1(), ne.getOp2());
		}
	}

}
