package nz.ac.wgtn.yamf.reporting.msoffice;


import nz.ac.wgtn.yamf.reporting.Reporter;
import java.util.*;

/**
 * Abstract reporter. Has some logic useful in subclasses.
 * @author jens dietrich
 */
public abstract class AbstractReporter implements Reporter {

    private Map<String,String> replacementRules = new LinkedHashMap<>(); // retain order

    public void replaceStringInReport(String oldValue,String newValue) {
        replacementRules.put(oldValue,newValue);
    }

    public void replaceUsernameInReport(String alias) {
        String userName = System.getProperty("user.name");
        replacementRules.put(userName,alias);
    }

    public void anonymiseMarker() {
        replaceUsernameInReport("$marker");
    }

    protected String sanitise(String txt) {
        if (txt==null) {
            return null;
        }
        if (replacementRules.isEmpty()) {
            return txt;
        }
        for (String k:replacementRules.keySet()) {
            String v = replacementRules.get(k);
            txt = txt.replace(k,v);
        }
        return txt;
    }

}
