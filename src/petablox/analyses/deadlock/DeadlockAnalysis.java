package petablox.analyses.deadlock;

import java.io.PrintWriter;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import soot.SootMethod;
import soot.Unit;

import petablox.project.Config;
import petablox.program.Program;
import petablox.project.ClassicProject;
import petablox.project.Petablox;
import petablox.project.OutDirUtils;
import petablox.project.analyses.JavaAnalysis;
import petablox.project.analyses.ProgramRel;

import petablox.util.ArraySet;
import petablox.util.graph.IPathVisitor;
import petablox.util.graph.ShortestPathBuilder;
import petablox.analyses.alias.CIObj;
import petablox.analyses.alias.ICICG;
import petablox.analyses.thread.ThrSenCICGAnalysis;
import petablox.analyses.alias.DomO;
import petablox.analyses.alloc.DomH;
import petablox.bddbddb.Rel.RelView;
import petablox.analyses.thread.DomA;
import petablox.analyses.invk.DomI;
import petablox.analyses.lock.DomL;
import petablox.analyses.method.DomM;
import petablox.util.SetUtils;
import petablox.util.soot.SootUtilities;

/**
 * Static deadlock analysis.
 * <p>
 * Outputs relation 'deadlock' containing each tuple (a1,l1,l2,a2,l3,l4) denoting a possible
 * deadlock between abstract thread a1, which acquires a lock at l1 followed by a lock at l2,
 * and abstract thread a2, which acquires a lock at l3 followed by a lock at l4.
 * <p>
 * Recognized system properties:
 * <ul>
 *   <li>petablox.deadlock.exclude.escaping (default is false).</li>
 *   <li>petablox.deadlock.exclude.parallel (default is false).</li>
 *   <li>petablox.deadlock.exclude.nonreent (default is false).</li>
 *   <li>petablox.deadlock.exclude.nongrded (default is false).</li>
 *   <li>petablox.print.results (default is false).</li>
 * </ul>
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(name="deadlock-java", consumes = { "syncLH" })
public class DeadlockAnalysis extends JavaAnalysis {
    private DomA domA;
    private DomH domH;
    private DomI domI;
    private DomL domL;
    private DomM domM;
    private ProgramRel relDeadlock;
    private ProgramRel relSyncLH;
    private ICICG thrSenCICG;
    private final Map<SootMethod, Set<SootMethod>> MMmap = new HashMap<SootMethod, Set<SootMethod>>();

    public void run() {
        boolean excludeParallel = Boolean.getBoolean("petablox.deadlock.exclude.parallel");
        boolean excludeEscaping = Boolean.getBoolean("petablox.deadlock.exclude.escaping");
        boolean excludeNonreent = Boolean.getBoolean("petablox.deadlock.exclude.nonreent");
        boolean excludeNongrded = Boolean.getBoolean("petablox.deadlock.exclude.nongrded");

        domA = (DomA) ClassicProject.g().getTrgt("A");
        domH = (DomH) ClassicProject.g().getTrgt("H");
        domI = (DomI) ClassicProject.g().getTrgt("I");
        domL = (DomL) ClassicProject.g().getTrgt("L");
        domM = (DomM) ClassicProject.g().getTrgt("M");
        
        relDeadlock = (ProgramRel) ClassicProject.g().getTrgt("deadlock");
        relSyncLH   = (ProgramRel) ClassicProject.g().getTrgt("syncLH");

        ThrSenCICGAnalysis thrSenCICGAnalysis =
            (ThrSenCICGAnalysis) ClassicProject.g().getTrgt("thrsen-cicg-java");
        ClassicProject.g().runTask(thrSenCICGAnalysis);
        thrSenCICG = thrSenCICGAnalysis.getCallGraph();

        if (excludeParallel)
            ClassicProject.g().runTask("deadlock-parallel-exclude-dlog");
        else
            ClassicProject.g().runTask("deadlock-parallel-include-dlog");
        if (excludeEscaping)
            ClassicProject.g().runTask("deadlock-escaping-exclude-dlog");
        else
            ClassicProject.g().runTask("deadlock-escaping-include-dlog");
        if (excludeNonreent)
            ClassicProject.g().runTask("deadlock-nonreent-exclude-dlog");
        else
            ClassicProject.g().runTask("deadlock-nonreent-include-dlog");
        if (excludeNongrded)
            ClassicProject.g().runTask("deadlock-nongrded-exclude-dlog");
        else
            ClassicProject.g().runTask("deadlock-nongrded-include-dlog");
        ClassicProject.g().runTask("deadlock-dlog");

        if (Config.printResults){}
            printResults();
    }

    private CIObj getPointsTo(int lIdx) {
        RelView view = relSyncLH.getView();
        view.selectAndDelete(0, lIdx);
        Iterable<Object> objs = view.getAry1ValTuples();
        Set<Unit> pts = SetUtils.newSet(view.size());
        for (Object o : objs)
            pts.add((Unit) o);
        view.free();
        return new CIObj(pts);
    }
    
    private void printResults() {
        final DomO domO = new DomO();
        domO.setName("O");
        
        PrintWriter out;

        // relDeadlock.load();

        // System.out.println(System.getProperty("user.dir"));
        // relDeadlock.print(System.getProperty("user.dir"));

        relDeadlock.load();

        out = OutDirUtils.newPrintWriter("deadlocks.txt");
        for (Object[] tuple : relDeadlock.getAryNValTuples()) {
            Unit [] lockA = {(Unit) tuple[1], (Unit) tuple[2]}, 
                    lockB = {(Unit) tuple[4], (Unit) tuple[5]};

            String [] lockA_str = new String [2], 
                      lockB_str = new String [2];

            for (int i = 0; i < 2; i++) { 
                lockA_str[i] = SootUtilities.toByteLocStr(lockA[i]);
                lockB_str[i] = SootUtilities.toByteLocStr(lockB[i]);
            }

            // Sort them
            Arrays.sort(lockA_str);
            Arrays.sort(lockB_str);

            // Sort them again
            if ( lockA_str[0].compareTo(lockB_str[0]) > 0 
                 || (
                   lockA_str[0].compareTo(lockB_str[0]) == 0 &&
                   lockA_str[1].compareTo(lockB_str[1]) > 0
                 )
               ) { 
                String [] tmp = lockA_str;
                lockA_str = lockB_str; 
                lockB_str = tmp; 
            }

            // Print them.
            out.print(lockA_str[0]); 
            out.print(",");
            out.print(lockA_str[1]); 
            out.print(";");
            out.print(lockB_str[0]); 
            out.print(",");
            out.print(lockB_str[1]); 
        }
        
        relDeadlock.close();
        out.close();        
    }

    private void addToMMmap(SootMethod m1, SootMethod m2) {
        Set<SootMethod> s = MMmap.get(m1);
        if (s == null) {
            s = new ArraySet<SootMethod>();
            MMmap.put(m1, s);
        }
        s.add(m2);
    }
}
