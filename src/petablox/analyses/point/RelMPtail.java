package petablox.analyses.point;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.Block;

import java.util.List;

import petablox.program.visitors.IMethodVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.soot.ICFG;
import petablox.util.soot.SootUtilities;

/**
 * Relation containing each tuple (m,p) such that p is the unique exit basic block of method m.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(name = "MPtail", sign = "M0,P0:M0xP0")
public class RelMPtail extends ProgramRel implements IMethodVisitor {
    public void visit(SootClass c) { }
    public void visit(SootMethod m) {
        if (m.isAbstract())
            return;
        ICFG cfg = SootUtilities.getCFG(m);
        List<Block> ls =  cfg.getTails();
        if(!ls.isEmpty()) {
        	Unit bx = cfg.getTails().get(0).getHead();
        	add(m, bx);
        }
    }
}
