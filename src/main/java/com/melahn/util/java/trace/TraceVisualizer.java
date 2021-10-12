package com.melahn.util.java.trace;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TraceVisualizer {

    protected static Logger logger;
    public static void main (String[] args) {
        logger = LogManager.getLogger("gg");
        logger.info("Hello Trace");
    }
}
