package petablox.analyses.thread;

import petablox.analyses.method.DomM;
import petablox.program.Program;
import petablox.project.Petablox;
import petablox.project.ClassicProject;
import petablox.project.analyses.JavaAnalysis;
import petablox.project.analyses.ProgramRel;
import soot.SootMethod;

/**
 * Static analysis computing reachable abstract threads.
 * <p>
 * Domain A is the domain of reachable abstract threads.
 * The 0th element does not denote any abstract thread; it is a placeholder for convenience.
 * The 1st element denotes the main thread.
 * The remaining elements denote threads explicitly created by calling the
 * {@code java.lang.Thread.start()} method; there is a separate element for each abstract
 * object to which the {@code this} argument of that method may point, as dictated by the
 * points-to analysis used.
 * <p>
 * Relation threadACM contains each tuple (a,c,m) such that abstract thread 'a' is started
 * at thread-root method 'm' in abstract context 'c'.  Thread-root method 'm' may be either:
 * <ul>
 *   <li>
 *     the main method, in which case 'c' is epsilon (element 0 in domain C), or
 *   </li>
 *   <li>
 *     the {@code java.lang.Thread.start()} method, in which case 'c' may be epsilon
 *     (if the call graph is built using 0-CFA) or it may be a chain of possibly
 *     interspersed call/allocation sites (if the call graph is built using k-CFA or
 *     k-object-sensitive analysis or a combination of the two).
 *   </li>
 * </ul>
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(
    name = "thread-java",
    consumes = { "nonMainThreadM" },
    produces = { "A", "threadAM" },
    namesOfSigns = { "threadAM" },
    signs = { "A0,M0:A0_M0" },
    namesOfTypes = { "A" },
    types = { DomA.class }
)
public class ThreadAnalysis extends JavaAnalysis {
    public void run() {
        ClassicProject project = ClassicProject.g();
        Program program = Program.g();
        DomM domM = (DomM) project.getTrgt("M");
        DomA domA = (DomA) project.getTrgt("A");
        domA.clear();
        domA.add(null);
        SootMethod mainMeth = program.getMainMethod();
        domA.add(mainMeth);
        ProgramRel relThreadM = (ProgramRel) project.getTrgt("nonMainThreadM");
        relThreadM.load();
        Iterable<SootMethod> tuples = relThreadM.getAry1ValTuples();
        for (SootMethod m : tuples)
            domA.add(m);
        relThreadM.close();
        domA.save();
        ProgramRel relThreadAM = (ProgramRel) project.getTrgt("threadAM");
        relThreadAM.zero();
        for (int aIdx = 1; aIdx < domA.size(); aIdx++) {
            SootMethod m = domA.get(aIdx);
            int mIdx = domM.indexOf(m);
            relThreadAM.add(aIdx, mIdx);
        }
        relThreadAM.save();
    }
}

