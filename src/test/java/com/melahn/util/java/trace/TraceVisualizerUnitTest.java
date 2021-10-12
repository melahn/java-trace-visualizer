package com.melahn.util.java.trace;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TraceVisualizerUnitTest {
 
    private final static String DIVIDER = "-------------------------------------";

    @BeforeAll
    static void setUp() {
        System.out.println(DIVIDER.concat(" UNIT TESTS START ").concat(DIVIDER));
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }

    @AfterAll
    static void cleanUp() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
        System.out.println(DIVIDER.concat(" UNIT TESTS END ").concat(DIVIDER));
    }

    @Test
    void normalTest() {
        TraceVisualizer t = new TraceVisualizer();
        assertEquals("com.melahn.util.java.trace.TraceVisualizer", t.getClass().getName());
        assertDoesNotThrow(()->TraceVisualizer.main(new String[0]));
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }
}
