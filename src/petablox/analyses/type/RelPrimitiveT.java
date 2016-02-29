package petablox.analyses.type;

import soot.Type;
import soot.NullType;
import soot.RefLikeType;
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
    name = "primitiveT",
    sign = "T0"
)
public class RelPrimitiveT extends ProgramRel {
    public void fill() {
        Program program = Program.g();
        IndexSet<Type> types = program.getTypes();
        for (Type t : types) {
            if (t != null && !(t instanceof NullType)
                          && !(t instanceof RefLikeType)) {
                    add(t);
            }
        }
    }
}
