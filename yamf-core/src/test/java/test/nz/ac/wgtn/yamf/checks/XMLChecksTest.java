package test.nz.ac.wgtn.yamf.checks;

import nz.ac.wgtn.yamf.commons.XML;
import java.io.File;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class XMLChecksTest {

    @Test
    public void testIsXMLFile1() throws Exception {
        File file = Utils.getResourceAsFile("xml/pom.xml");
        assertTrue(XML.isXML(file));
    }

    @Test
    public void testIsXMLFile2() throws Exception {
        File file = Utils.getResourceAsFile("xml/pom-broken-xml.xml");
        assertFalse(XML.isXML(file));
    }

    @Test
    public void testIsXMLFile3() throws Exception {
        File file = Utils.getResourceAsFile("xml/pom-invalid-xml.xml");
        assertTrue(XML.isXML(file));
    }

    @Test  @Disabled
    public void testIsValidXMLFile1() throws Exception {
        // this test depends on the availability of the network to access the schema
        // and is therefore flaky
        // TODO: build a suitable precondition
        File file = Utils.getResourceAsFile("xml/pom.xml");
        assertTrue(XML.isValidXML(file));
    }

    @Test
    public void testIsValidXMLFile2() throws Exception {
        File file = Utils.getResourceAsFile("xml/pom-broken-xml.xml");
        // not XML so precondition fails
        assertThrows(IllegalArgumentException.class, () -> XML.isValidXML(file));
    }

    @Test
    public void testIsValidXMLFile3() throws Exception {
        File file = Utils.getResourceAsFile("xml/pom-invalid-xml.xml");
        assertFalse(XML.isValidXML(file));
    }

}
