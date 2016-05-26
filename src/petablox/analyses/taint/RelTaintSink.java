package petablox.analyses.taint;

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
 * Relation containing all methods marked as tainted sinks by the user.
 *
 * @author Joe Cox (cox@cs.ucla.edu)
 */
@Petablox(
    name = "taintSink",
    sign = "M0"
)
public class RelTaintSink extends ProgramRel implements IMethodVisitor {
    private ArrayList<String> methodDescriptors;
    public RelTaintSink() {
        try {
            if (Config.taintSinkFile.equals("")) {
                throw new PetabloxException("Taint sink file is required to build taintSink relation");
            } else {
                String file = Config.workDirName + File.separator + Config.taintSinkFile;
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
