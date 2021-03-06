package petablox.analyses.field;

import petablox.program.visitors.IFieldVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import soot.SootClass;
import soot.SootField;

/**
 * Relation containing all instance (as opposed to static) fields.
 * It does not include the distinguished hypothetical field
 * <tt>arrayElem</tt> that is regarded as accessed whenever an
 * array element is read/written.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(
    name = "instF",
    sign = "F0"
)
public class RelInstF extends ProgramRel implements IFieldVisitor {
    public void visit(SootClass c) { }
    public void visit(SootField f) { 
        if (!f.isStatic())
            add(f);
    }
}
