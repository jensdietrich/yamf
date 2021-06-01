package nz.ac.wgtn.yamf;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.platform.launcher.TestIdentifier;

import java.util.Collection;
import java.util.Collections;

/**
 * Mechanism to record attachements that can be inlined into reports.
 * WARNING: this does not support testing using multiple threads.
 * @author jens dietrich
 */
public class Attachments {

    private static TestIdentifier currentTest = null;
    private static Multimap<TestIdentifier,Attachment> attachments = HashMultimap.create();

    // for testing, disable precondition checks
    private static boolean TEST_MODE = false;

    public static void setTestTestMode() {
        TEST_MODE = true;
    }

    static void startTest(TestIdentifier test) {
        Preconditions.checkState(TEST_MODE || currentTest==null);
        currentTest = test;
    }

    static void endTest(TestIdentifier test) {
        Preconditions.checkState(TEST_MODE || currentTest==test);
        currentTest = null;
    }

    static void reset() {
        Preconditions.checkState(TEST_MODE || currentTest==null); // call endTest first
        attachments = HashMultimap.create();
    }

    public static void add(Attachment attachment) {
        Preconditions.checkState(TEST_MODE || currentTest!=null);
        attachments.put(currentTest,attachment);
    }

    public static void addAll(Collection<Attachment> attachments2) {
        Preconditions.checkState(TEST_MODE || currentTest!=null);
        attachments.putAll(currentTest,attachments2);
    }

    static Collection<Attachment> getAttachments (TestIdentifier test) {
        return Collections.unmodifiableCollection(attachments.get(test));
    }



}
