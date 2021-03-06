package petablox.analyses.syntax;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;

import petablox.program.visitors.IExprVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

@Petablox(name = "IntExpr", sign="IntConst0")
public class RelIntExpr extends ProgramRel implements IExprVisitor {
    @Override
    public void visit(SootClass c) { }

    @Override
    public void visit(SootMethod m) { }

    @Override
    public void visit(Unit u) { }

    public void visit(Value e) {
        if (e instanceof IntConstant)
            add(e);
    }
}
