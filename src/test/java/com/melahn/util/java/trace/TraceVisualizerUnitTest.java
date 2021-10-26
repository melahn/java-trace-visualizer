package com.melahn.util.java.trace;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.melahn.util.test.TraceVisualizerTestUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TraceVisualizerUnitTest {
 
    private static final String DIVIDER = "-------------------------------------";
    private static final String EXPECTED = TraceVisualizerTestUtil.generateRandomString(10);
    private static final PrintStream INITIAL_OUT = System.out;
    private static final String EXAMPLE_JDB_OUT_FILENAME = "./src/test/resource/example-single-thread-trace-file.jdb.out.txt";
    private static final String EXAMPLE_TEXT_OUT_FILENAME = "./src/test/resource/example-single-thread-trace-file.txt";
    private static final String TEST_PUML_OUT_FILENAME = "./target/example-single-thread-trace-file.puml";
    private static final String TEST_TEXT_OUT_FILENAME = "./target/example-single-thread-trace-file.txt";
    private static final Path TEST_OUT_PATH = Paths.get("./target/test/out");

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(TEST_OUT_PATH);
        System.out.println(DIVIDER.concat(" UNIT TESTS START ").concat(DIVIDER));
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }

    @AfterAll
    static void cleanUp() throws IOException {
        TraceVisualizerTestUtil.cleanDirectory(TEST_OUT_PATH);
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
        System.out.println(DIVIDER.concat(" UNIT TESTS END ").concat(DIVIDER));
    }

    @Test
    void normalTest() {
        TraceVisualizer t = new TraceVisualizer();
        assertEquals("com.melahn.util.java.trace.TraceVisualizer", t.getClass().getName());
        // no parameters
        assertDoesNotThrow(()->TraceVisualizer.main(new String[0]));
        // generate a visualized text trace using the example jdb out
        String[] a1 = new String[]{"-i", EXAMPLE_JDB_OUT_FILENAME, "-o", TEST_TEXT_OUT_FILENAME};
        assertDoesNotThrow(()->TraceVisualizer.main(a1));
        assertTrue(Files.exists(Paths.get(TEST_TEXT_OUT_FILENAME)));
        // generate a visualized text trace using the example jdb out
        String[] a2 = new String[]{"-i", EXAMPLE_JDB_OUT_FILENAME, "-o", TEST_PUML_OUT_FILENAME};
        assertDoesNotThrow(()->TraceVisualizer.main(a2));
        assertTrue(Files.exists(Paths.get(TEST_PUML_OUT_FILENAME)));
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }

    @Test
    void helpTest() throws IOException {
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(o));
            TraceVisualizer.main(new String[0]); // An empty array should show help
            assertTrue(
                    TraceVisualizerTestUtil.streamContains(o, "Usage:"));
            System.setOut(INITIAL_OUT);
        }
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(o));
            TraceVisualizer.main(new String[]{"-h"}); // the -h switch should show help
            assertTrue(
                    TraceVisualizerTestUtil.streamContains(o, "Usage:"));
            System.setOut(INITIAL_OUT);
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
            System.setOut(INITIAL_OUT);
        }
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(o));
            TraceVisualizer.main(new String[]{"-i"}); // an input option missing an argument is a Parse Exception
            assertTrue(
                    TraceVisualizerTestUtil.streamContains(o, "Parse Exception: Missing argument for option: i"));
            System.setOut(INITIAL_OUT);
        }
        // both options are correctly specified with -g
        assertDoesNotThrow(()->TraceVisualizer.main(new String[]{"-i", "./src/test/resource/example-trace-file.txt", "-o", "output-file", "-g"}));
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
