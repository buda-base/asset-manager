package io.bdrc.am.audit;

import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class logtest {

    @Ignore
    @Test
    public void slf4j() {
        Logger logger = LoggerFactory.getLogger("Frelm");

        logger.info("greetings from {} ", "info");
        logger.error("greetings from {} ", "error");
        logger.debug("greetings from {} ", "debug");
    }

    @Ignore @Test
    public void log4jTest () {
//        org.apache.logging.log4j.Logger logger = LogManager.getLogger(logtest.class);
        org.apache.logging.log4j.Logger thing1 = LogManager.getLogger("thing1");
        org.apache.logging.log4j.Logger thing2 = LogManager.getLogger("thing2");
        try {
            thing1.info("greetings from {} ", "info");
            thing2.info("greetings from {} ", "2info");
            thing1.error("gYo reetings from {} ", "error");
            thing2.error("gYo reetings from {} ", "2error");
            thing1.fatal("Dying over here");
            thing2.fatal("2Dying over here");
            thing1.debug("greetings from {} ", "2debug");
            thing2.debug("greetings from {} ", "2debug");
        }
        catch (Exception e) {
            thing1.error(e);
        }

    }
}
