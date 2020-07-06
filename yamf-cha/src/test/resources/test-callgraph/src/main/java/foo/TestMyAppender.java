package foo;

import org.apache.log4j.Logger;

public class TestMyAppender {
    public static void main (String[] args) {
        Logger logger = Logger.getLogger(TestMyAppender.class);
        logger.addAppender(new MyAppender());
        logger.debug("Hello World");
    }
}
