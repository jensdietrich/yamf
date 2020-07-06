package foo;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Dummy appender for testing purposes.
 */
public class AppenderImpl2 extends AppenderSkeleton {
    protected void append(LoggingEvent loggingEvent) {

    }

    public void close() {

    }

    public boolean requiresLayout() {
        return false;
    }
}
