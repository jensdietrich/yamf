package nz.ac.vuw.yamf.commons;

import org.zeroturnaround.exec.ProcessResult;

import java.util.regex.Pattern;

/**
 * JDK - related utilities.
 * @author jens dietrich
 */
public class JDK {

    public static Pattern VERSION_PATTERN = Pattern.compile("\\d*\\.\\d*.\\d((_|-)[a-zA-Z0-9]+)?");

    // get the version of Java in the path, could be different from the version of used to run this program
    public static String getJavaVersion() throws Exception {
        ProcessResult result = OS.exe("java","-version");
        if (result.getExitValue()!=0) {
            throw new IllegalStateException("java not available -- \"java -version\" has failed - details " + System.lineSeparator() + result.getOutput().getString());
        }
        String output = result.outputString();

        // first token matching pattern
        String[] lines = output.split(System.lineSeparator());
        assert lines.length>0;

        for (String line:lines) {
            String[] tokens = line.split(" ");
            for (String token:tokens) {
                token = token.replace("\"","").trim(); // trim will also remove \n etc
                if (VERSION_PATTERN.matcher(token).matches()) {
                    return token;
                }
            }
        }
        return null;

//        String firstLine = lines[0];
//        int i1 = firstLine.indexOf("\"");
//        int i2 = firstLine.indexOf("\"",i1+1);
//        return firstLine.substring(i1+1,i2);
    }

    // get the version of Javac in the path
    public static String getJavacVersion() throws Exception {
        ProcessResult result = OS.exe("javac","-version");
        if (result.getExitValue()!=0) {
            throw new IllegalStateException("javac not available -- \"javac -version\" has failed - details " + System.lineSeparator() + result.getOutput().getString());
        }
        String output = result.outputString();

        // first token matching pattern
        String[] lines = output.split(System.lineSeparator());
        assert lines.length>0;

        for (String line:lines) {
            String[] tokens = line.split(" ");
            for (String token:tokens) {
                token = token.replace("\"","").trim(); // trim will also remove \n etc
                if (VERSION_PATTERN.matcher(token).matches()) {
                    return token;
                }
            }
        }
        return null;
    }
}
