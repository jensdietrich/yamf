package foo;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class MyAppender extends WriterAppender {
    @Override
    public void append(LoggingEvent loggingEvent) {
        super.append(loggingEvent);
        MyHelper.write(loggingEvent);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
