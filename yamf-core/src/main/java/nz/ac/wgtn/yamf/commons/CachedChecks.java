package nz.ac.wgtn.yamf.commons;

import java.io.*;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Utility to cache the results of checks.
 * Use case: when marking a submission using mvn , one might want to penalise the existence of /target in the submission
 * (as binaries built should not be added to a repo).
 * When a marking script is first evaluated, this is easy to pick up. However, when running mvn tasks as part of the script.
 * the state of target/ is altered. This is an issue both ways: target/ could be created when it was not there, or target/ could be erased
 * (if the scripts runs mvn clean) when it was initially present.
 * The idea behind this utility is to run those checks, and cache the results in a local file in a submission folder.
 * By default, the filename used is .yamf-cached-checks.properties
 * @author jens dietrich
 */
public class CachedChecks {

    public static final String CACHE_FILE_NAME = ".yamf-cached-checks.properties";

    public static boolean check(File submissionFolder, String propertyName, Supplier<Boolean> check) {
        File cache = new File(submissionFolder,CACHE_FILE_NAME);
        String value = null;
        boolean dirty = false;
        Properties properties = new Properties();
        if (cache.exists()) {
            try (FileReader reader = new FileReader(cache)) {
                properties.load(reader);
                value = properties.getProperty(propertyName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // compute
        if (value==null) {
            value = String.valueOf(check.get());
            properties.setProperty(propertyName,value);
            dirty = true;
        }

        // write
        if (dirty) {
            try (FileWriter writer = new FileWriter(cache)) {
                properties.store(writer, "written by " + CachedChecks.class.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assert value!=null;
        return Boolean.valueOf(value);

    }
}
