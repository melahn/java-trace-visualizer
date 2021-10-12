package com.melahn.util.java.trace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.melahn.util.test.TraceVisualizerTestUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

class TraceVisualizerIntegrationTest {

    private List<String> args = null;
    private final String targetTestDirName = "target/integration-test";
    private final Path targetTestPath = Paths.get(targetTestDirName);
    private final String className = "com.melahn.util.java.trace.TraceVisualizer";
    private final Path JaCocoAgentPath = Paths.get("", "lib/org.jacoco.agent-0.8.7-runtime").toAbsolutePath();
    private final String JaCocoAgentString = JaCocoAgentPath.toString()
            .concat(".jar=destfile=../jacoco.exec,append=true");
    private final Path logFilePath = Paths.get(TARGET_TEST_DIR_NAME, "sub-process-out.txt");
    private final TraceVisualizerTestUtil utility = new TraceVisualizerTestUtil();
    private static final String DIVIDER = "-------------------------------------";
    private static final String TARGET_TEST_DIR_NAME = "target/integration-test";
    private static final Path TARGET_TEST_PATH = Paths.get(TARGET_TEST_DIR_NAME);


     /**
     * Performs Integration Test setup by cleaning, then recreating the test directory.
     */
    @BeforeAll
    static void setUp() {
        System.out.println(DIVIDER.concat(" INTEGRATION TESTS START ").concat(DIVIDER));
        try {
            TraceVisualizerTestUtil.cleanDirectory(TARGET_TEST_PATH);
            Files.createDirectories(TARGET_TEST_PATH);
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }
    /**
     * Performs Integration Test cleanup.
     * 
     * A placeholder for now.
     */
    @AfterAll
    static void cleanUp() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
        System.out.println(DIVIDER.concat(" INTEGRATION TESTS END ").concat(DIVIDER));
    }

    /**
     * Tests the no error, normal case in the shaded jar.
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    void normalTest() throws InterruptedException, IOException {
        args = Arrays.asList(""); // no parms yet
        int exitValue = utility.createProcess(args, new String[][] { new String[] {}, new String[] {} }, null, JaCocoAgentString,
                className, targetTestPath, logFilePath);
        assertEquals(0,exitValue);
        System.out.println(new Throwable().getStackTrace()[0].getMethodName().concat(" completed"));
    }
}
