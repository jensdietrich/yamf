package nz.ac.wgtn.yamf;


import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A file with some details that might be useful to report.
 * @author jens dietrich
 */
public class Attachment {
    private String name = null;
    private File file = null;
    private List<String> content = null;
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

    public Attachment(String name, List<String> content, String contentType) {
        this.name = name;
        this.content = content;
        this.contentType = contentType;
    }

    public Attachment(String name, Throwable throwable) {
        this.name = name;
        this.contentType = "text/plain";
        this.content = Stream.of(throwable.getStackTrace()).map(e -> e.toString()).collect(Collectors.toList());
        if (throwable.getMessage()!=null) {
            this.content.add(0, throwable.getMessage());
        }
    }


    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public List<String> getContent() {
        if (this.content!=null) {
            return this.content;
        }
        else {
            assert file!=null;
            assert file.exists();
            try {
                return Files.readLines(file, Charset.defaultCharset());
            }
            catch (IOException x) {
                System.err.println("Error reading attachment " + file);
                x.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
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
                Objects.equals(content, that.content) &&
                Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, file, content, contentType);
    }

    @Override
    public String toString() {
        return "Attachment{" + file.getName() + '}';
    }
}
