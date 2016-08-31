package petablox.analyses.field;

import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.tagkit.SourceFileTag;
import petablox.program.visitors.IFieldVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramDom;
import petablox.util.Utils;
import petablox.util.soot.SootUtilities;

/**
 * Domain of fields.
 * <p>
 * The 0th element in this domain (null) denotes a distinguished hypothetical
 * field that is regarded as accessed whenever an array element is accessed.
 * This field can be used by analyses that do not distinguish between different
 * elements of the same array.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(name = "F")
public class DomF extends ProgramDom<SootField> implements IFieldVisitor {
    @Override
    public void init() {
        // Reserve index 0 for the distinguished hypothetical field representing all array elements
        getOrAdd(null);
    }

    @Override
    public void visit(SootClass c) { }

    @Override
    public void visit(SootField f) {
        getOrAdd(f);
    }

    @Override
    public String toFIString(SootField f) {
    	StringBuilder sb = new StringBuilder();
    	boolean printId = Utils.buildBoolProperty("petablox.printrel.printID", false);
    	if(printId) sb.append("(" + indexOf(f) + ")");
    	if (f != null) sb.append(f.getName() + "@" + f.getDeclaringClass().getName());
    	return sb.toString();
    }
    
    @Override
    public String toXMLAttrsString(SootField f) {
        String sign;
        String file;
        int line;
        if (f == null) {
            sign = "[*]";
            file = "";
            line = 0;
        } else {
            SootClass c = f.getDeclaringClass();
            sign = c.getName() + "." + f.getName();
            file = SootUtilities.getSourceFile(c);
            line = 0; // TODO
        }
        return "sign=\"" + sign +
            "\" file=\"" + file +
            "\" line=\"" + line + "\"";
    }
}
