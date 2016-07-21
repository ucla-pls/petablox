package petablox.analyses.lock;

import soot.SootClass;
import soot.SootMethod;
import soot.toolkits.graph.Block;
import soot.Unit;
import soot.tagkit.SourceFileTag;
import soot.tagkit.LineNumberTag;
import soot.jimple.internal.JEnterMonitorStmt;
import petablox.analyses.method.DomM;
import petablox.program.visitors.IAcqLockInstVisitor;
import petablox.project.Petablox;
import petablox.project.ClassicProject;
import petablox.project.Config;
import petablox.project.analyses.ProgramDom;
import petablox.util.soot.ICFG;
import petablox.util.soot.SootUtilities;
import petablox.util.soot.JEntryExitNopStmt;
import petablox.util.Utils;

/**
 * Domain of all lock acquire points, including monitorenter quads and entry basic blocks of synchronized methods.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(name = "L", consumes = { "M" })
public class DomL extends ProgramDom<Unit> implements IAcqLockInstVisitor {
    protected DomM domM;
    protected SootMethod ctnrMethod;

    @Override
    public void init() {
        domM = (DomM) (Config.classic ?  ClassicProject.g().getTrgt("M") : consumes[0]);
    }

    @Override
    public void visit(SootClass c) { }

    @Override
    public void visit(SootMethod m) {
        if (m.isAbstract())
            return;
        if (m.isSynchronized()) {
            ICFG cfg = SootUtilities.getCFG(m);
            Unit head = cfg.getHeads().get(0).getHead();
            add(head);
        }
    }

    @Override
    public void visitAcqLockInst(Unit u) {
        add(u);
    }

    @Override
    public String toUniqueString(Unit u) {
        return SootUtilities.toByteLocStr(u);                             
    }
    
    @Override
    public String toFIString(Unit u) {		    
    	StringBuilder sb = new StringBuilder();
    	boolean printId = Utils.buildBoolProperty("petablox.printrel.printID", false);
    	if (printId) sb.append("(" + indexOf(u) + ")");
    	if(u instanceof JEntryExitNopStmt)
    		sb.append("SYNC METH");
    	else if(u instanceof JEnterMonitorStmt)
    		sb.append("MONITOR ENTER");
    	sb.append(":"+SootUtilities.getMethod(u).getName() + "@" + SootUtilities.getMethod(u).getDeclaringClass().getName());
    	return sb.toString();
    }

    @Override
    public String toXMLAttrsString(Unit u) {
        SootMethod m = SootUtilities.getMethod(u);    
        String file = SootUtilities.getSourceFile(m.getDeclaringClass());
        int line = SootUtilities.getLineNumber(u);
        int mIdx = domM.indexOf(m);
        return "file=\"" + file + "\" " + "line=\"" + line + "\" " + "Mid=\"M" + mIdx + "\"";
    }
}
