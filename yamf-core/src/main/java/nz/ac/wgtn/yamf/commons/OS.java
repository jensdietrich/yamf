package nz.ac.wgtn.yamf.commons;

import nz.ac.wgtn.yamf.Attachment;
import nz.ac.wgtn.yamf.Attachments;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities to interact with the OS, in particular to run commands.
 * author jens dietrich
 */
public class OS {

    public static StartedProcess start(String... command) throws Exception {
        return new ProcessExecutor()
            .readOutput(true)
            .command(command)
            .start();
    }

    /**
     * Only start process, to be finished by application. Use case: starting a web server (jetty) with Maven.
     * @param workingDir
     * @param command
     * @return
     * @throws Exception
     */
    public static StartedProcess start(File workingDir, String... command) throws Exception {
        return new ProcessExecutor()
            .readOutput(true)
            .directory(workingDir)
            .command(command)
            .start();
    }

    public static ProcessResult exe(String... command) throws Exception {
        ProcessResult result = new ProcessExecutor()
            .readOutput(true)
            .command(command)
            .execute();
        recordErrorLog(result);
        return result;
    }

    public static ProcessResult exe(File workingDir, String... command) throws Exception {
        ProcessResult result = new ProcessExecutor()
            .readOutput(true)
            .directory(workingDir)
            .command(command)
            .execute();
        recordErrorLog(result);
        return result;
    }

    private static void recordErrorLog(ProcessResult result) {
        if (result.getExitValue()!=0) {
            String output = result.outputString();
            if (output!=null) {
                List<String> lines = new BufferedReader(new StringReader(output))
                    .lines()
                    .collect(Collectors.toList());
                Attachment attachment = new Attachment("console output",lines,"text/plain");
                Attachments.add(attachment);
            }
        }
    }
}
