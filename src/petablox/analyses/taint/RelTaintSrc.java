package petablox.analyses.method;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import soot.SootClass;
import soot.SootMethod;
import petablox.program.visitors.IMethodVisitor;
import petablox.project.Config;
import petablox.project.Petablox;
import petablox.project.PetabloxException;
import petablox.project.analyses.ProgramRel;

/**
 * Relation containing all methods marked as tainted sources by the user.
 *
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "taintSrc",
    sign = "M0"
)
public class RelTaintSrc extends ProgramRel implements IMethodVisitor {
    private ArrayList<String> methodDescriptors;
    public RelTaintSrc() {
        try {
            if (Config.taintSrcFile.equals("")) {
                throw new PetabloxException("Taint source file is required to build taintSrc relation");
            } else {
                String file = Config.workDirName + File.separator + Config.taintSrcFile;
                BufferedReader reader = new BufferedReader(new FileReader(file));
                methodDescriptors = new ArrayList<String>();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    methodDescriptors.add(line);
                }
            }
        } catch (Exception ex) {
            throw new PetabloxException(ex.getMessage());
        }
    }
    public void visit(SootClass c) {}
    public void visit(SootMethod m) {
        if (methodDescriptors.contains(m.getSignature())) {
            add(m);
        }
    }
}
