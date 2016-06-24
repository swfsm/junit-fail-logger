package com.github.swfsm.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoLogger {

    private static final Logger LOG = LoggerFactory.getLogger("junit.Failure");

    public static void logFailure(String message) {
        LOG.error(message);
    }

}
