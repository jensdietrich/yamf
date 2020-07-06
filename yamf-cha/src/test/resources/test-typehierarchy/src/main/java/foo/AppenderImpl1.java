package foo;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Dummy appender for testing purposes.
 */
public class AppenderImpl1 implements Appender {
    public void addFilter(Filter filter) {

    }

    public Filter getFilter() {
        return null;
    }

    public void clearFilters() {

    }

    public void close() {

    }

    public void doAppend(LoggingEvent loggingEvent) {

    }

    public String getName() {
        return null;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {

    }

    public ErrorHandler getErrorHandler() {
        return null;
    }

    public void setLayout(Layout layout) {

    }

    public Layout getLayout() {
        return null;
    }

    public void setName(String s) {

    }

    public boolean requiresLayout() {
        return false;
    }
}
