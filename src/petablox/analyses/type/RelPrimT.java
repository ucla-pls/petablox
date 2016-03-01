package petablox.analyses.type;

import soot.Type;
import soot.PrimType;
import petablox.program.Program;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import petablox.util.IndexSet;

/**
 * Relation containing each primitive type.
 *
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "primT",
    sign = "T0"
)
public class RelPrimT extends ProgramRel {
    public void fill() {
        Program program = Program.g();
        IndexSet<Type> types = program.getTypes();
        for (Type t : types) {
            if (t != null && t instanceof PrimType) {
                    add(t);
            }
        }
    }
}
