package petablox.analyses.syntax;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Trap;
import soot.jimple.internal.JTrap;

import petablox.program.visitors.IMethodVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;

@Petablox(name = "Trap", sign = "M0,T0,P0,P1,P2:M0_T0xP0xP1xP2")
public class RelTrap extends ProgramRel implements IMethodVisitor {
    @Override
    public void visit(SootClass m) { }

    @Override
    public void visit(SootMethod m) {
        for (Trap trap : m.retrieveActiveBody().getTraps()){
            if (trap instanceof JTrap) {
                add(m, trap.getException().getType(), trap.getBeginUnit(), trap.getEndUnit(),
                        trap.getHandlerUnit());
            }
        }
    }
}
