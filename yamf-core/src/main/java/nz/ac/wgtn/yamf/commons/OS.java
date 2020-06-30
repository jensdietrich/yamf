package nz.ac.wgtn.yamf.commons;

import nz.ac.wgtn.yamf.Attachment;
import nz.ac.wgtn.yamf.Attachments;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
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
