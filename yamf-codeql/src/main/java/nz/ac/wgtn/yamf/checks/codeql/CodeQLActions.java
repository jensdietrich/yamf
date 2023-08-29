package nz.ac.wgtn.yamf.checks.codeql;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.Attachments;
import nz.ac.wgtn.yamf.commons.OS;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.exec.ProcessResult;
import java.io.File;
import java.io.IOException;

/**
 * CodeQL-related actions.
 * @author jens dietrich
 */
public class CodeQLActions {

    final static String CODEQL_HOME_ENVIRON_VAR = "CODEQL_HOME";
    static final String CODEQL_DATABASE_NAME = ".codeqldb";

    private static File CODEQL_HOME = null;
    public static File CODEQL_CMD = null;

    static {
        System.out.println("Initialising codeql module");
        String _codeql = System.getenv(CODEQL_HOME_ENVIRON_VAR);
        if (_codeql==null) {
            System.err.println("The environment variable " + CODEQL_HOME_ENVIRON_VAR + " must be set and must point to the installation folder of codeql");
        }
        else {
            File _codeqlFolder = new File(_codeql);
            if (!_codeqlFolder.exists()) {
                System.err.println("CodeQl installation folder defined in CODEQL_HOME does not exist");
            }
            else if (!_codeqlFolder.isDirectory()) {
                System.err.println("CodeQl installation folder defined in CODEQL_HOME is not a folder");
            }
            else if (!new File(_codeqlFolder, "codeql").exists()) {
                System.err.println("CodeQl installation folder defined in CODEQL_HOME does not contain expected codeql command");
            }
            else if (!new File(_codeqlFolder, "codeql").canExecute()) {
                System.err.println("CodeQl installation folder defined in CODEQL_HOME does not contain executable *executable* codeql");
            }
            else {
                CODEQL_HOME = _codeqlFolder;
                CODEQL_CMD = new File(CODEQL_HOME, "codeql");
            }
        }
    }

    // for testing only TODO: remove
    public static void main(String[] arg) throws Exception {
        Attachments.setTestTestMode();
        CodeQLActions.checkCodeQLAvailability();
    }

    /**
     * Computed a boolean that should be use to guard codeql-related checks (in an assertion).
     * @author jens dietrich
     */
    public static boolean checkCodeQLAvailability() throws Exception {
        if (CODEQL_CMD==null) {
            return false;
        }
        ProcessResult result = OS.exe(CODEQL_CMD.getAbsolutePath(), "--version");
        // System.out.println(result.getExitValue() == 0);
        System.out.println(result.outputString());
        return result.getExitValue() == 0;
    }

    public static final File getCodeqlDb(File projectFolder) {
        return new File(projectFolder,CODEQL_DATABASE_NAME);
    }

    public static void resetDatabase (File projectFolder)  {
        File codeDBFolder = new File(projectFolder,CODEQL_DATABASE_NAME);
        if (codeDBFolder.exists() && codeDBFolder.isDirectory()) {
            try {
                System.out.println("Deleting existing codeql database: " + codeDBFolder.getAbsolutePath());
                FileUtils.deleteDirectory(codeDBFolder);
            } catch (IOException e) {
                System.err.println("Error deleting existing codeql database: " + codeDBFolder.getAbsolutePath());
                e.printStackTrace();
            }
        }
        System.out.println("Deleting existing codeql database: nothing to do, folder does not exist: " + codeDBFolder.getAbsolutePath());
    }

    // codeql database create $DB -l java -c 'mvn clean install'
    public static void createDatabase (File projectFolder,String buildCommand) throws Exception {
        Preconditions.checkState(CODEQL_CMD!=null,"codeql is not available, check logs for codeql initialisation errors and readme for how to setup codeql");
        resetDatabase(projectFolder);
        String[] args = new String[8];
        args[0]=CODEQL_CMD.getAbsolutePath();
        args[1]="database";
        args[2]="create";
        args[3]=getCodeqlDb(projectFolder).getAbsolutePath();
        args[4]="-l";
        args[5]="java";
        args[6]="-c";
        args[7]=buildCommand;

        ProcessResult result = OS.exe(args);
        System.out.println(result.outputString());
        if (result.getExitValue()==0) {
            System.out.println("CodeQL DB created in " + new File(projectFolder,CODEQL_DATABASE_NAME).getAbsolutePath());
        }
        else {
            throw new RuntimeException("Error creating codeql db in " + new File(projectFolder,CODEQL_DATABASE_NAME).getAbsolutePath());
        }
    }

    // codeql database create $DB -l java -c 'mvn clean install'
    public static void createDatabaseForMvnProject (File projectFolder) throws Exception {
        createDatabase(projectFolder,"mvn clean compile");
    }

}
