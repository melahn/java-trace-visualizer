package com.melahn.util.java.trace;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.melahn.util.test.TraceVisualizerTestUtil;

class TraceVisualizerUnitTest {

    public static class UnitTestTracePrinter extends TraceVisualizerBasePrinter {

        UnitTestTracePrinter(String r, String v, String s, String t) throws TraceVisualizerException {
            super(r, v, s, t);
        }

        UnitTestTracePrinter(String r, String v, String t) throws TraceVisualizerException {
            super(r, v, t);
        }

        UnitTestTracePrinter() {
            super();
        }

        public void printHeader() throws TraceVisualizerException {super.printHeader();}

        public void printFooter() throws TraceVisualizerException {super.printFooter();}
    
        public void printTraceStats() throws TraceVisualizerException {super.printTraceStats();}
    
        public void printVisualizedTraceNode(TraceNode n) throws TraceVisualizerException {super.printVisualizedTraceNode(n);};
    
        public void processRawTraceFile() throws TraceVisualizerException {super.processRawTraceFile();}
    }
 
    Logger logger = LogManager.getLogger();
    private static final String DIVIDER = "-------------------------------------";
    private static final String EXPECTED = TraceVisualizerTestUtil.generateRandomString(10);
    private static final PrintStream INITIAL_OUT = System.out;
    private static final String EXAMPLE_JDB_OUT_FILENAME = "./src/test/resource/example-single-thread-trace-file.jdb.out.txt";
    private static final String EXAMPLE_TEXT_OUT_FILENAME = "./src/test/resource/example-single-thread-trace-file.txt";
    private static final String TEST_PUML_OUT_FILENAME = "./target/example-single-thread-trace-file.puml";
    private static final String TEST_STATS_OUT_FILENAME = "./target/example-single-thread-trace-file-stats.txt";
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
        // no parameters
        assertDoesNotThrow(()->TraceVisualizer.main(new String[0]));
        // generate a visualized text trace using the example jdb out
        String[] a1 = new String[]{"-i", EXAMPLE_JDB_OUT_FILENAME, "-o", TEST_TEXT_OUT_FILENAME};
        assertDoesNotThrow(()->TraceVisualizer.main(a1));
        assertTrue(Files.exists(Paths.get(TEST_TEXT_OUT_FILENAME)));
        // generate a visualized puml trace using the example jdb out
        String[] a2 = new String[]{"-i", EXAMPLE_JDB_OUT_FILENAME, "-o", TEST_PUML_OUT_FILENAME};
        assertDoesNotThrow(()->TraceVisualizer.main(a2));
        assertTrue(Files.exists(Paths.get(TEST_PUML_OUT_FILENAME)));
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }

    @Test
    void statsTest() {
        TraceVisualizer t = new TraceVisualizer();
        // generate a visualized stats file using a text trace using the example jdb out
        String[] a1 = new String[]{"-i", EXAMPLE_JDB_OUT_FILENAME, "-o", TEST_TEXT_OUT_FILENAME, "-s", TEST_STATS_OUT_FILENAME};
        assertDoesNotThrow(()->TraceVisualizer.main(a1));
        assertTrue(Files.exists(Paths.get(TEST_STATS_OUT_FILENAME)));
        // generate a visualized stats file using a puml trace using the example jdb out
        String[] a2 = new String[]{"-i", EXAMPLE_JDB_OUT_FILENAME, "-o", TEST_PUML_OUT_FILENAME, "-s", TEST_STATS_OUT_FILENAME};
        assertDoesNotThrow(()->TraceVisualizer.main(a2));
        assertTrue(Files.exists(Paths.get(TEST_STATS_OUT_FILENAME)));
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }

    @Test
    void IOExceptionsTest() throws IOException, TraceVisualizerException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("./target/foo.txt"));
        BufferedWriter sbw = spy(bw);
        // the spy throws an IO Exception when asked to write any string 
        doThrow(IOException.class).when(sbw).write(anyString());
        try (MockedStatic<Files> mf = org.mockito.Mockito.mockStatic(Files.class)) {
            mf.when(() -> Files.newBufferedWriter(any(Path.class), any(Charset.class), any(OpenOption.class))).thenReturn(sbw);
            TraceVisualizerTextPrinter tvtp = new TraceVisualizerTextPrinter(EXAMPLE_JDB_OUT_FILENAME, TEST_TEXT_OUT_FILENAME, null);
            tvtp.setLogger(logger);
            assertThrows(TraceVisualizerException.class, () -> tvtp.printHeader());
            assertThrows(TraceVisualizerException.class, () -> tvtp.printFooter());
            TraceNode t = new TraceNode(0, "foo", "0", 1, null);
            assertThrows(TraceVisualizerException.class, () -> tvtp.printVisualizedTraceNode(t));
            TraceVisualizerPlantUMLPrinter tvpp = new TraceVisualizerPlantUMLPrinter(EXAMPLE_JDB_OUT_FILENAME, TEST_TEXT_OUT_FILENAME, null);
            tvpp.setLogger(logger);
            assertThrows(TraceVisualizerException.class, () -> tvpp.printHeader());
            assertThrows(TraceVisualizerException.class, () -> tvpp.printFooter());
            assertThrows(TraceVisualizerException.class, () -> tvpp.printVisualizedTraceNode(t));
        }
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }

    @Test 
    void TraceVisualizeBasePrinterTest() throws IOException, TraceVisualizerException {
        UnitTestTracePrinter uttp1 = new UnitTestTracePrinter(EXAMPLE_JDB_OUT_FILENAME, TEST_TEXT_OUT_FILENAME, null);
        assertDoesNotThrow(()->uttp1.printHeader());
        assertDoesNotThrow(()->uttp1.printFooter());
        TraceNode t = new TraceNode(0, "foo", "0", 1, null);
        assertDoesNotThrow(()->uttp1.printVisualizedTraceNode(t));
        assertDoesNotThrow(()->uttp1.processRawTraceFile());
        uttp1.setTraceThreadName(null);
        assertEquals(TraceVisualizerBasePrinter.DEFAULT_THREAD_NAME, uttp1.getTraceThreadName());
        uttp1.setTraceThreadName("foo");
        assertEquals("foo", uttp1.getTraceThreadName());
        UnitTestTracePrinter uttp2 = new UnitTestTracePrinter();
        assertNotNull(uttp2);
        UnitTestTracePrinter uttp3 = new UnitTestTracePrinter(EXAMPLE_JDB_OUT_FILENAME, TEST_TEXT_OUT_FILENAME, TEST_STATS_OUT_FILENAME, null);
        uttp3.processRawTraceFile();
        assertTrue(Files.exists(Paths.get(TEST_STATS_OUT_FILENAME)));
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
        TraceVisualizer.setStatsFilename(e);
        assertEquals(e, TraceVisualizer.getStatsFilename());
        TraceVisualizer.setGenerateImage(true);
        assertEquals(true, TraceVisualizer.getGenerateImage());
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }
}
