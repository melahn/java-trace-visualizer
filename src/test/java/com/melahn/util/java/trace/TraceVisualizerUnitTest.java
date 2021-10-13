package com.melahn.util.java.trace;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.melahn.util.test.TraceVisualizerTestUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TraceVisualizerUnitTest {
 
    private static final String EXPECTED = TraceVisualizerTestUtil.generateRandomString(10);
    private static final PrintStream INITIALOUT = System.out;
    private static final String DIVIDER = "-------------------------------------";

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

    @Test
    void helpTest() throws IOException {
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(o));
            TraceVisualizer.main(new String[0]); // An empty array should show help
            assertTrue(
                    TraceVisualizerTestUtil.streamContains(o, "Usage:"));
            System.setOut(INITIALOUT);
        }
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(o));
            TraceVisualizer.main(new String[]{"-h"}); // the -h switch should show help
            assertTrue(
                    TraceVisualizerTestUtil.streamContains(o, "Usage:"));
            System.setOut(INITIALOUT);
        }
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }


    @Test
    void optionsTest() throws IOException {
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(o));
            TraceVisualizer.main(new String[]{"-i", "input-file"}); // only one required option is a Parse Exception
            assertTrue(
                    TraceVisualizerTestUtil.streamContains(o, "Parse Exception: Both the -i and -o options must be specified"));
            System.setOut(INITIALOUT);
        }
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(o));
            TraceVisualizer.main(new String[]{"-i"}); // an input option missing an argument is a Parse Exception
            assertTrue(
                    TraceVisualizerTestUtil.streamContains(o, "Parse Exception: Missing argument for option: i"));
            System.setOut(INITIALOUT);
        }
        // both options are correctly specified with -g
        assertDoesNotThrow(()->TraceVisualizer.main(new String[]{"-i", "input-file", "-o", "output-file", "-g"}));
        assertEquals(true, TraceVisualizer.getGenerateImage());
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }
    
    @Test
    void getterSetterTest() {
        String e = EXPECTED;
        TraceVisualizer.setInputFilename(e);
        assertEquals(e, TraceVisualizer.getInputFilename());
        TraceVisualizer.setOutputFilename(e);
        assertEquals(e, TraceVisualizer.getOutputFilename());
        TraceVisualizer.setGenerateImage(true);
        assertEquals(true, TraceVisualizer.getGenerateImage());
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }
}
