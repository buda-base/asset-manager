package io.bdrc.am.audit.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class shell {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("main");
        Logger t1 = LoggerFactory.getLogger("thing1");
        Logger t2 = LoggerFactory.getLogger("thing2");

        logger.info("Greetings from Main");
        t1.info("Et tu thing1");
        t2.info("Et tu thing2");

    }
}
