package petablox.analyses.primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import soot.SootClass;
import soot.SootMethod;
import soot.Local;
import soot.PrimType;
import soot.tagkit.SourceFileTag;
import petablox.analyses.method.DomM;
import petablox.program.visitors.IMethodVisitor;
import petablox.project.Petablox;
import petablox.project.ClassicProject;
import petablox.project.Config;
import petablox.project.analyses.ProgramDom;
import petablox.util.Utils;
import petablox.util.soot.SootUtilities;

/**
 * Domain of local variables of any type.
 * 
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(name = "X", consumes = { "M" })
public class DomX extends ProgramDom<Local> implements IMethodVisitor {
    protected DomM domM;
    protected Map<Local, SootMethod> varToMethodMap;

    public SootMethod getMethod(Local x) {
        return varToMethodMap.get(x);
    }

    @Override
    public void init() {
        domM = (DomM) (Config.classic ? ClassicProject.g().getTrgt("M") : consumes[0]);
        varToMethodMap = new HashMap<Local, SootMethod>();
    }

    @Override
    public void visit(SootClass c) {}

    @Override
    public void visit(SootMethod m) {
        if (!m.isConcrete())
            return;
        Iterator<Local> itr = SootUtilities.getLocals(m).iterator();
        while (itr.hasNext()) {
        	Local x = itr.next();
    		varToMethodMap.put(x, m);
        	add(x);
        }
    }

    @Override
    public String toUniqueString(Local x) {
        return x + "!" + getMethod(x);
    }

    @Override
    public String toFIString(Local x) {
    	StringBuilder sb = new StringBuilder();
    	boolean printId = Utils.buildBoolProperty("petablox.printrel.printID", false);
    	if (printId)
            sb.append("(" + indexOf(x) + ")");
    	sb.append("LCL:" + getMethod(x).getName() + "@" + getMethod(x).getDeclaringClass().getName());
    	return sb.toString();
    }
    
    @Override
    public String toXMLAttrsString(Local x) {
        SootMethod m = getMethod(x);
        int mIdx = domM.indexOf(m);
        String file =((SourceFileTag)m.getDeclaringClass().getTags().get(0)).getSourceFile();
        List<Integer> lineArr = SootUtilities.getLineNumber(m,x);
        String line = "";
        if (lineArr == null) {
            line = String.valueOf(SootUtilities.getLineNumber(m,0));
        } else {
            for (int xline : lineArr) {
                if (!line.equals("")) line += ",";
                line += xline;
            }
        }
        return "file=\"" + file + "\" " + "line=\"" + line + "\" " + 
            "name=\"" + SootUtilities.getRegName(getMethod(x),x) + "\" " + "Mid=\"M" + mIdx + "\"";
    }
}
