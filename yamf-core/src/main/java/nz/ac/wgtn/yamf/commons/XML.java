package nz.ac.wgtn.yamf.commons;

import com.google.common.base.Preconditions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

/**
 * Several checks and utilities related to the processing of XML files.
 * @author jens dietrich
 */
public class XML {

    public static NodeList evalXPath(File file, String xpath) throws Exception {
        Preconditions.checkArgument(isXML(file),"file is not an xml file: " + file.getAbsolutePath());
        FileInputStream fileIS = new FileInputStream(file);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(fileIS);
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (NodeList) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODESET);
    }

    /**
     * Evaluate xpath, assume that the expression yield a single node.
     * If no node is in the result set, return null.
     * If multiple nodes are returned, throw an IllegalArgumentException.
     * @param file
     * @param xpath
     * @return
     * @throws Exception
     */
    public static String evalXPathSingleNode(File file, String xpath) throws Exception {
        Preconditions.checkArgument(isXML(file),"file is not an xml file: " + file.getAbsolutePath());
        FileInputStream fileIS = new FileInputStream(file);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(fileIS);
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = evalXPath(file,xpath);
        if (nodeList.getLength()==0) {
            return null;
        }
        else if (nodeList.getLength()>1) {
            throw new IllegalArgumentException("XPath query too general, resulted in multiple nodes in result set");
        }
        else {
            return nodeList.item(0).getTextContent();
        }
    }

    /**
     * Evaluate xpath, assume that the expression yield a single node.
     * If no node is in the result set, multiple nodes are returned, or the value cannot be converted to an int,
     * throws a runtime exception
     * @param file
     * @param xpath
     * @return
     * @throws Exception
     */
    public static int evalXPathSingleNodeAsInt(File file, String xpath) throws Exception {
        Preconditions.checkArgument(isXML(file),"file is not an xml file: " + file.getAbsolutePath());
        FileInputStream fileIS = new FileInputStream(file);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(fileIS);
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = evalXPath(file,xpath);
        if (nodeList.getLength()==0) {
            throw new IllegalArgumentException("XPath query too specific, resulted in empty result set");
        }
        else if (nodeList.getLength()>1) {
            throw new IllegalArgumentException("XPath query too general, resulted in multiple nodes in result set");
        }
        else {
            String value = nodeList.item(0).getTextContent();
            return Integer.parseInt(value);
        }
    }

    /**
     * Check whether this is an xml file.
     * @param file
     * @return
     * @throws Exception
     */
    public static boolean isXML(File file) throws Exception {
        Preconditions.checkArgument(file.exists(),"file does not exist: " + file.getAbsolutePath());
        try {
            FileInputStream fileIS = new FileInputStream(file);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            builder.parse(fileIS);
            return true;
        }
        catch (SAXException e) {
            return false;
        }
    }

    public static boolean isValidXML(File file) throws Exception {
        Preconditions.checkArgument(isXML(file),"file is not an xml file: " + file.getAbsolutePath());
        try {
            Source xmlInput=new StreamSource(new FileReader(file));
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema();
            Validator validator = schema.newValidator();
            validator.validate(xmlInput);
            return true;
        }
        catch (SAXException e) {
            return false;
        }
    }

}
