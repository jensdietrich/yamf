package nz.ac.wgtn.yamf.checks.jee;

import nz.ac.wgtn.yamf.checks.jbytecode.JAnnotation;
import nz.ac.wgtn.yamf.checks.jbytecode.JClass;
import nz.ac.wgtn.yamf.commons.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JEE Actions.
 * @author jens dietrich
 */
public class JEEActions {

    /**
     * Compute the URL pattern mappings defined either in a class using annotations, or in web.xml. If there are multiple patterns, then they are concatenated using ','.
     * @param webxml
     * @param clazz
     * @return a map associating class names with URL patterns
     * @throws Exception
     */
    public static Map<String,String> getServletToURLMappings(File webxml,JClass clazz) throws Exception {
        Map<String,String> map = new HashMap<>();
        // web.xml is not required when annotations are used -- so this step is skipped if the file does not exist
        if (webxml!=null && webxml.exists()) {
            map.putAll(getServletToURLMappingsFromWebXML(webxml));
        }
        map.putAll(getServletToURLMappingsFromAnnotations(clazz));
        return map;
    }

    /**
     * Compute the URL pattern mappings defined in web.xml. If there are multiple patterns, then they are concatenated using ','.
     * @param webxml
     * @return a map associating class names with URL patterns
     * @throws Exception
     */
    public static Map<String,String> getServletToURLMappingsFromWebXML(File webxml) throws Exception {

        Map<String,String> servlet2Id = new HashMap<>();
        NodeList nodeList = XML.evalXPath(webxml,"/web-app/servlet");
        for (int i=0;i<nodeList.getLength();i++) {
            Node node = nodeList.item(i);
            String servletClassName = null;
            String servletName = null;
            //if (node instanceof Element && node.getNodeName().equals("servlet")) {
                Element element = (Element)node;
                NodeList children = element.getElementsByTagName("servlet-name");
                if (children.getLength()>0) {
                    servletName = children.item(0).getTextContent().trim();
                }
                children = element.getElementsByTagName("servlet-class");
                if (children.getLength()>0) {
                    servletClassName = children.item(0).getTextContent().trim();
                }
            //}
            if (servletClassName!=null && servletName!=null) {
                servlet2Id.put(servletClassName,servletName);
            }
        }

        Map<String,String> id2url = new HashMap<>();
        nodeList = XML.evalXPath(webxml,"/web-app/servlet-mapping");
        for (int i=0;i<nodeList.getLength();i++) {
            Node node = nodeList.item(i);
            List<String> urlMappings = new ArrayList<>();
            String servletName = null;
           // if (node instanceof Element && node.getNodeName().equals("servlet")) {
                Element element = (Element)node;
                NodeList children = element.getElementsByTagName("servlet-name");
                if (children.getLength()>0) {
                    servletName = children.item(0).getTextContent().trim();
                }
                children = element.getElementsByTagName("url-pattern");
                for (int j=0;j<children.getLength();j++) {
                    String urlMapping = children.item(j).getTextContent().trim();
                    urlMappings.add(urlMapping);
                }
            //}
            if (urlMappings.size()>0 && servletName!=null) {
                id2url.put(servletName,urlMappings.stream().collect(Collectors.joining(",")));
            }
        }

        // stitch maps
        Map<String,String> servlet2url = new HashMap<>();
        for (String servlet:servlet2Id.keySet()) {
            String name = servlet2Id.get(servlet);
            String url = id2url.get(name);
            servlet2url.put(servlet,url);
        }

        return servlet2url;
    }

    /**
     * Compute the URL pattern mappings defined using JEE class annotations. If there are multiple patterns, then they are concatenated using ','.
     * @param clazz
     * @return a map associating class names with URL patterns
     * @throws Exception
     */
    public static Map<String,String> getServletToURLMappingsFromAnnotations(JClass clazz)  {

        Map<String,String> map = new HashMap<>();
        for (JAnnotation annotation:clazz.getAnnotations()) {
            if (annotation.getName().equals("javax.servlet.annotation.WebServlet")) {
                Object object = annotation.getProperty("urlPatterns");
                // flatten list
                if (object instanceof List) {
                    List<Object> list = (List)object;
                    // there can be a list of URL patterns, those are concatenated
                    String value = list.stream().map(e -> Objects.toString(e).trim()).collect(Collectors.joining(","));
                    map.put(clazz.getName(),value);
                }
            }
        }
        return map;
    }
}
