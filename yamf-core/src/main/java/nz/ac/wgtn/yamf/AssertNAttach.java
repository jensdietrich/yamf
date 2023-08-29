package nz.ac.wgtn.yamf;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Improved junit5 assertions creating attachments.
 * The typical use case to capture console output.
 */
public class AssertNAttach {

    public static void assertSame(Object obj1,Object obj2,String message,String attachmentName,String details) throws Exception {
        if (obj1!=obj2) {
            Attachment attachment = new Attachment(attachmentName, lines(details), "text/plain");
            Attachments.add(attachment);
        }
        org.junit.jupiter.api.Assertions.assertSame(obj1, obj2,message);
    }

    public static void assertSame(int i1,int i2,String message,String attachmentName,String details) throws Exception {
        if (i1!=i2) {
            Attachment attachment = new Attachment(attachmentName, lines(details), "text/plain");
            Attachments.add(attachment);
        }
        org.junit.jupiter.api.Assertions.assertSame(i1, i2,message);
    }

    public static void assertTrue(boolean value,String message,String attachmentName,String details) throws Exception {
        if (!value) {
            Attachment attachment = new Attachment(attachmentName, lines(details), "text/plain");
            Attachments.add(attachment);
        }
        org.junit.jupiter.api.Assertions.assertTrue(value,message);
    }

    private static List<String> lines(String message) throws Exception {
        if (message==null) {
            return Collections.EMPTY_LIST;
        }
        else {
            String[] lines = message.split("\\R");
            return Stream.of(lines).collect(Collectors.toList());
        }
    }
}
