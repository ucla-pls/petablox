package petablox.analyses.syntax;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.internal.JTableSwitchStmt;

import petablox.program.visitors.ITableSwitchInstVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

@Petablox(name = "TableSwitchCaseInst", sign = "P0,EXPR0,IntConst0,P1:P0_EXPR0xIntConst0xP1")
public class RelTableSwitchCaseInst extends ProgramRel implements ITableSwitchInstVisitor {
    @Override
    public void visit(SootClass m) { }

    @Override
    public void visit(SootMethod m) { }

    @Override
    public void visit(Unit u) { }

    public void visit(JTableSwitchStmt s) {
        Value key = s.getKey();
        int lower = s.getLowIndex();

        for (Unit target : s.getTargets()){
            add(s, key, IntConstant.v(lower), target);
            lower++;
        }
    }
}
