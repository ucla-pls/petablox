package petablox.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import petablox.bddbddb.RelSign;
import petablox.project.analyses.ProgramDom;
import petablox.project.analyses.ProgramRel;
import petablox.util.Utils;

/**
 * Parser for Petablox annotations on classes defining program analyses.
 *
 * The annotation specifies aspects of the analysis such as its name,
 * its consumed and produced targets, etc.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class PetabloxAnnotParser {
    private static final String ERROR = "ERROR: PetabloxAnnotParser: @Petablox annotation of class '%s': %s";
    private static final String SIGN_EMPTY = "Method sign() cannot return empty string.";
    private static final String SIGN_NON_EMPTY = "Method sign() cannot return non-empty string.";
    private static final String PRODUCES_NON_EMPTY = "Method produces() cannot return non-empty string.";
    private static final String CONTROLS_NON_EMPTY = "Method controls() cannot return non-empty string.";
    private static final String AMBIGUOUS_TYPE = "Method namesOfSigns() implicitly declares name '%s' as having type '%s' whereas method namesOfTypes() declares it as having incompatible type '%s'."; 
    private static final String TYPES_ARYLEN_MISMATCH = "Methods namesOfTypes() and types() return arrays of different lengths.";
    private static final String SIGNS_ARYLEN_MISMATCH = "Methods namesOfSigns() and signs() return arrays of different lengths.";
    private static final String NAMES_OF_TYPES_FORBIDDEN = "Method namesOfTypes() cannot return the same name as that returned by name()";
    private static final String NAMES_OF_TYPES_REPEATING = "Method namesOfTypes() cannot return a name ('%s') multiple times.";
    private static final String NAMES_OF_SIGNS_FORBIDDEN = "Method namesOfSigns() cannot return the same name as that returned by name(); use sign().";
    private static final String NAMES_OF_SIGNS_REPEATING = "Method namesOfSigns() cannot return a name ('%s') multiple times.";

    private final Class type;
    private String name;
    private String prescriber;
    private List<String> consumes;
    private List<String> produces;
    private List<String> controls;
    private Map<String, RelSign> nameToSignMap;
    private Map<String, Class> nameToTypeMap;
    private boolean hasNoErrors;
    
    private void error(String format, Object... args) {
        String msg = String.format(format, args);
        Messages.log(ERROR, type.getName(), msg);
        hasNoErrors = false;
    }
    /**
     * Constructor.
     * 
     * @param    type    A class annotated with a Petablox annotation.
     */
    public PetabloxAnnotParser(Class type) {
        this.type = type;
    }
    /**
     * Parses this Petablox annotation.
     * 
     * @return    true iff the Petablox annotation parses successfully.
     */
    public boolean parse() {
        Petablox chord = (Petablox) type.getAnnotation(Petablox.class);
        assert (chord != null);
        hasNoErrors = true;
        name = chord.name();

        nameToTypeMap = new HashMap<String, Class>();
        if (Config.multiPgmMode &&
            (Utils.isSubclass(type, ProgramRel.class) || Utils.isSubclass(type, ProgramDom.class))
           )
        	name = Config.multiTag + name;
        nameToTypeMap.put(name, type);

        nameToSignMap = new HashMap<String, RelSign>();
        String sign = chord.sign();
        RelSign relSign = null;
        if (Utils.isSubclass(type, ProgramRel.class)) {
            if (sign.equals(""))
                error(SIGN_EMPTY);
            else {
                relSign = parseRelSign(sign);
                nameToSignMap.put(name, relSign);
            }
        } else if (!sign.equals(""))
            error(SIGN_NON_EMPTY);

        prescriber = chord.prescriber();
        if (prescriber.equals(""))
            prescriber = name;

        {
            String[] a = chord.consumes();
            consumes = new ArrayList<String>(a.length);
            // NOTE: domains MUST be added BEFORE any declared consumed
            // targets to 'consumes' if this annotation is on a subclass
            // of ProgramRel; ModernProject relies on this invariant.
            if (relSign != null) {
                for (String domName : relSign.getDomKinds())
                    consumes.add(domName);
            }
            for (String s : a)
                consumes.add(processName(s));
        }

        {
            String[] a = chord.produces();
            produces = new ArrayList<String>(a.length);
            for (String s : a)
                produces.add(processName(s));
        }

        {
            String[] a = chord.controls();
            controls = new ArrayList<String>(a.length);
            for (String s : a)
                controls.add(processName(s));
        }

        // program rels and doms should not declare any produces/controls
        if (Utils.isSubclass(type, ProgramRel.class) ||
            Utils.isSubclass(type, ProgramDom.class)) {
            if (produces.size() > 0) {
                error(PRODUCES_NON_EMPTY);
                produces.clear();
            }
            produces.add(name);
            if (controls.size() > 0) {
                error(CONTROLS_NON_EMPTY);
                controls.clear();
            }
        }

        String[] namesOfTypes = chord.namesOfTypes();
        Class [] types = chord.types();
        if (namesOfTypes.length != types.length) {
            error(TYPES_ARYLEN_MISMATCH);
        } else {
            for (int i = 0; i < namesOfTypes.length; i++) {
                String name2 = namesOfTypes[i];
                if (name2.equals(".")) {
                    error(NAMES_OF_TYPES_FORBIDDEN);
                    continue;
                }
                if (Config.multiPgmMode) name2 = Config.multiTag + name2;
                if (name2.equals(name)) {
                    error(NAMES_OF_TYPES_FORBIDDEN);
                    continue;
                }
                if (nameToTypeMap.containsKey(name2)) {
                    error(NAMES_OF_TYPES_REPEATING, name2);
                    continue;
                }
                nameToTypeMap.put(name2, types[i]);
            }
        }

        String[] namesOfSigns = chord.namesOfSigns();
        String[] signs = chord.signs();
        if (namesOfSigns.length != signs.length) {
            error(SIGNS_ARYLEN_MISMATCH);
        } else {
            for (int i = 0; i < namesOfSigns.length; i++) {
                String name2 = namesOfSigns[i];
                if (name2.equals(".")) {
                    error(NAMES_OF_SIGNS_FORBIDDEN);
                    continue;
                }
                if (Config.multiPgmMode) name2 = Config.multiTag + name2;
                if (name2.equals(name)) {
                    error(NAMES_OF_SIGNS_FORBIDDEN);
                    continue;
                }
                if (nameToSignMap.containsKey(name2)) {
                    error(NAMES_OF_SIGNS_REPEATING, name2);
                    continue;
                }
                Class type2 = nameToTypeMap.get(name2);
                if (type2 != null) {
                    if (!Utils.isSubclass(type2, ProgramRel.class)) {
                        error(AMBIGUOUS_TYPE, name2, ProgramRel.class.getName(), type2.getName());
                        continue;
                    }
                }
                RelSign relSign2 = parseRelSign(signs[i]);
                if (relSign2 != null)
                    nameToSignMap.put(name2, relSign2);
            }
        }

        return hasNoErrors;
    }

    private String processName(String s) {
        int i = s.indexOf('!');
        String name2;
        if (i == -1) {
            name2 = s;
            if (Config.multiPgmMode) name2 = Config.multiTag + name2;
        } else {
            name2 = s.substring(0, i);
            if (Config.multiPgmMode) name2 = Config.multiTag + name2;
            String t = s.substring(i + 1);
            if (t.startsWith("sign=")) {
                RelSign relSign2 = parseRelSign(t.substring(5));
                nameToSignMap.put(name2, relSign2);
            }
        }  
        return name2;
    }
    
    private RelSign parseRelSign(String sign) {
        int i = sign.indexOf(':');
        String domOrder;
        if (i != -1) {
            domOrder = sign.substring(i + 1);
            sign = sign.substring(0, i);
        } else
            domOrder = null;
        String[] domNamesAry = sign.split(",");
        String[] modDomNamesAry = new String[domNamesAry.length];
        for (int j = 0; j < domNamesAry.length; j++) {
        	if (Config.multiPgmMode)
        		modDomNamesAry[j] = Config.multiTag + domNamesAry[j];
        	else
        		modDomNamesAry[j] = domNamesAry[j];
        }
        if (modDomNamesAry.length == 1)
            domOrder = domNamesAry[0];
        RelSign newRs;
        try {
        	String modOrder = RelSign.fixMultiPgmVarOrder(domOrder);
            newRs = new RelSign(modDomNamesAry, modOrder);
        } catch (RuntimeException ex) {
            error(ex.getMessage());
            newRs = null;
        }
        return newRs;
    }
    /**
     * Provides the name specified by this Petablox annotation of the
     * associated analysis.
     * 
     * @return    The name specified by this Petablox annotation of the
     * associated analysis.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Provides the name of the control target specified by this
     * Petablox annotation as prescribing the associated analysis.
     * 
     * @return    The name of the control target specified by this
     * Petablox annotation as prescribing the associated analysis.
     */
    public String getPrescriber() {
        return prescriber;
    }
    /**
     * Provides the names of data targets specified by this Petablox
     * annotation as consumed by the associated analysis.
     * 
     * @return    The names of data targets specified by this Petablox
     * annotation as consumed by the associated analysis.
     */
    public List<String> getConsumes() {
        return consumes;
    }
    /**
     * Provides the names of data targets specified by this Petablox
     * annotation as produced by the associated analysis.
     * 
     * @return    The names of data targets specified by this Petablox
     * annotation as produced by the associated analysis.
     */
    public List<String> getProduces() {
        return produces;
    }
    /**
     * Provides the names of control targets specified by this Petablox
     * annotation as produced by the associated analysis.
     * 
     * @return    The names of control targets specified by this Petablox
     * annotation as produced by the associated analysis.
     */
    public List<String> getControls() {
        return controls;
    }
    /**
     * Provides a partial map specified by this Petablox annotation from
     * names of program relation targets consumed/produced by the
     * associated analysis to their signatures.
     * 
     * @return    A partial map specified by this Petablox annotation from
     * names of program relation targets consumed/produced by the
     * associated analysis to their signatures.
     */
    public Map<String, RelSign> getNameToSignMap() {
        return nameToSignMap;
    }
    /**
     * Provides a partial map specified by this Petablox annotation from
     * names of data targets consumed/produced by the associated
     * analysis to their types.
     * 
     * @return    A partial map specified by this Petablox annotation from
     * names of data targets consumed/produced by the associated
     * analysis to their types.
     */
    public Map<String, Class> getNameToTypeMap() {
        return nameToTypeMap;
    }
};
