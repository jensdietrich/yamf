package nz.ac.wgtn.yamf;


import com.google.common.base.Preconditions;

import java.io.File;
import java.util.Objects;

/**
 * A file with some details that might be useful to report.
 * @author jens dietrich
 */
public class Attachment {
    private String name = null;
    private File file = null;
    private String contentType = null;

    public Attachment(String name, File file, String contentType) {
        Preconditions.checkArgument(name!=null);
        Preconditions.checkArgument(file!=null);
        Preconditions.checkArgument(file.exists());
        Preconditions.checkArgument(contentType!=null);
        this.name = name;
        this.file = file;
        this.contentType = contentType;

    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(file, that.file) &&
                Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, file, contentType);
    }

    @Override
    public String toString() {
        return "Attachment{" + file.getName() + '}';
    }
}
