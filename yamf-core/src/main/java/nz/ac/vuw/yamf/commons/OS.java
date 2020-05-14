package nz.ac.vuw.yamf.commons;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import java.io.*;

/**
 * Utilities to interact with the OS, in particular to run commands.
 * author jens dietrich
 */
public class OS {

    public static ProcessResult exe(String... command) throws Exception {
        return new ProcessExecutor()
            .readOutput(true)
            .command(command)
            .execute();
    }

    public static ProcessResult exe(File workingDir, String... command) throws Exception {
        return new ProcessExecutor()
            .readOutput(true)
            .directory(workingDir)
            .command(command)
            .execute();
    }


}
