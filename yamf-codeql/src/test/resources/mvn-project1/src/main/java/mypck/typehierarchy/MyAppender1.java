package mypck.typehierarchy;

import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;

import java.io.Serializable;

public class MyAppender1 extends MyAbstractAppender {

    public void append(LogEvent event) {

    }

    public String getName() {
        return null;
    }

    public Layout<? extends Serializable> getLayout() {
        return null;
    }

    public boolean ignoreExceptions() {
        return false;
    }

    public ErrorHandler getHandler() {
        return null;
    }

    public void setHandler(ErrorHandler handler) {

    }

    public State getState() {
        return null;
    }

    public void initialize() {

    }

    public void start() {

    }

    public void stop() {

    }

    public boolean isStarted() {
        return false;
    }

    public boolean isStopped() {
        return false;
    }
}
