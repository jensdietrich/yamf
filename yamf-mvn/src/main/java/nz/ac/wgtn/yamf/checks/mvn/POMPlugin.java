package nz.ac.wgtn.yamf.checks.mvn;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a plugin.
 * @author jens dietrich
 */
public class POMPlugin {

    public static POMPlugin from(Node xmlNode) {
        POMPlugin dep = new POMPlugin();
        NodeList children = xmlNode.getChildNodes();
        for (int i=0;i<children.getLength();i++) {
            Node node = children.item(i);
            if (node instanceof Element) {
                if (node.getNodeName().equals("groupId")) {
                    dep.groupId = node.getTextContent();
                }
                else if (node.getNodeName().equals("artifactId")) {
                    dep.artifactId = node.getTextContent();
                }
                else if (node.getNodeName().equals("version")) {
                    dep.version = node.getTextContent();
                }
            }
        }
        return dep;
    }

    public POMPlugin() {
    }

    public POMPlugin(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public static List<POMPlugin> from(NodeList xmlNodes) {
        List<POMPlugin> deps = new ArrayList<>(xmlNodes.getLength());
        for (int i=0;i<xmlNodes.getLength();i++) {
            Node node = xmlNodes.item(i);
            POMPlugin dep = from(node);
            deps.add(dep);
        }
        return deps;
    }

    private String groupId = null;
    private String artifactId = null;
    private String version = null;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        POMPlugin plugin = (POMPlugin) o;
        return Objects.equals(groupId, plugin.groupId) &&
                Objects.equals(artifactId, plugin.artifactId) &&
                Objects.equals(version, plugin.version) ;
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }
}
