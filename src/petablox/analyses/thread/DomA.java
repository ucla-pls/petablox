package petablox.analyses.thread;

import soot.SootMethod;
import petablox.analyses.method.DomM;
import petablox.project.ClassicProject;
import petablox.project.analyses.ProgramDom;
import petablox.util.Utils;

/**
 * Domain of abstract threads.
 * <p>
 * An abstract thread is a triple (o,c,m) denoting the thread whose abstract object
 * is 'o' and which starts at method 'm' in abstract context 'c'.
 *
 * @see petablox.analyses.thread.ThreadsAnalysis
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class DomA extends ProgramDom<SootMethod> {
    private DomM domM;

    @Override
    public String toFIString(SootMethod m) {  
    	StringBuilder sb = new StringBuilder();
    	boolean printId = Utils.buildBoolProperty("petablox.printrel.printID", false);
    	if(printId) sb.append("(" + indexOf(m) +")");
    	sb.append(m.getName() + "@" + m.getDeclaringClass().getName());
    	return sb.toString();
    }
    
    @Override
    public String toXMLAttrsString(SootMethod m) {
        if (m == null) return "";
        if (domM == null) domM = (DomM) ClassicProject.g().getTrgt("M");
        int mIdx = domM.indexOf(m);
        return "Mid=\"M" + mIdx + "\"";
    }
}
