package com.melahn.util.java.trace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TraceVisualizerBasePrinter {
    int depth = 1;
    Logger logger = LogManager.getLogger();
    TraceNode parent = null;
    BufferedReader rawTraceFileReader = null;
    Map<Integer, TraceNode> traceNodes = new LinkedHashMap<>();
    Map<String, String> traceStats = new HashMap<>();
    BufferedWriter traceStatsFileWriter = null;
    String traceThreadName = null;
    BufferedWriter visualizedTraceFileWriter = null;
    static final int INDENT_INCREMENT = 5;
    public static final String DEFAULT_THREAD_NAME = "main";

    /**
     * Constructor that accepts the trace file name, the visualised trace file name,
     * the stats file name and the trace thread name.
     * 
     * @param r the name of the raw trace flile
     * @param v the name of the visualized trace file
     * @param s the name of the stats file
     * @param t the name of the trace thread (may be null)
     * 
     * @throws TraceVisualizerException if the constructor fails
     */
    TraceVisualizerBasePrinter(String r, String v, String s, String t) throws TraceVisualizerException {
        setCommonProperties(r, v, t);
        setTraceStatsFile(s);
        logger.debug("TraceVisualizerBasePrinter created using '{}' '{}' '{}' '{}'", r, v, s, t);
    }

    /**
     * Constructor that accepts the trace file name, the visualized trace file name
     * and the trace thread name.
     * 
     * @param r the name of the raw trace file
     * @param v the name of the visualized trace file
     * @param t the name of the trace thread (may be null)
     * 
     * @throws TraceVisualizerException if the constructor fails
     */
    TraceVisualizerBasePrinter(String r, String v, String t) throws TraceVisualizerException {
        setCommonProperties(r, v, t);
        logger.debug("TraceVisualizerBasePrinter created using '{}' '{}' '{}'", r, v, t);
    }

    /**
     * Base Constructor.
     */
    TraceVisualizerBasePrinter() {
        setLogger(LogManager.getLogger());
        logger.info("TraceVisualizerBasePrinter created");
    }

    /**
     * Sets properties common to all the constructors.
     * 
     * @param r the name of the raw trace file
     * @param v the name of the visualized trace file
     * @param t the name of the trace thread
     */
    private void setCommonProperties(String r, String v, String t) throws TraceVisualizerException {
        setLogger(LogManager.getLogger());
        setRawTraceFile(r);
        setVisualizedTraceFile(v);
        setTraceThreadName(t);
    }

    /**
     * Process the raw trace file.
     * 
     * @throws TraceVisualizerException should an error occur proceesing the trace
     *                                  file.
     */
    public void processRawTraceFile() throws TraceVisualizerException {
        logger.debug("Process Raw Trace file");
        String t = "";
        int i = 0;
        parent = new TraceNode(0, "start", "0", depth++, null);
        traceNodes.put(0, parent);
        try {
            while (t != null) {
                t = rawTraceFileReader.readLine();
                if (t != null) {
                    processRawTraceLine(i++, t);
                }
            }
            printVisualizedTraceFile();
        } catch (IOException e) {
            throw new TraceVisualizerException("IO Exception: ".concat(e.getLocalizedMessage()));
        }
    }

    /**
     * Using the information in the trace nodes map, print the visualized trace.
     */
    private void printVisualizedTraceFile() throws TraceVisualizerException {
        printHeader();
        for (Map.Entry<Integer, TraceNode> e : traceNodes.entrySet()) {
            TraceNode n = e.getValue();
            printVisualizedTraceNode(n);
            n.isPrinted = true;
        }
        printFooter();
    }

    /**
     * Prints the collected stats.
     * 
     * @throws TraceVisualizerException if the stats could not be printed
     */
    public void printTraceStats() throws TraceVisualizerException {
        try {
            if (traceStatsFileWriter != null) {
                traceStatsFileWriter.close();
                logger.debug("Trace Stats Printed");
            }
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Sets the name of the raw trace file which causes a reader to be created.
     * 
     * @param t the raw trace file name
     * @throws TraceVisualizerException if a reader cannot be obtained
     */
    public void setRawTraceFile(String t) throws TraceVisualizerException {
        try {
            rawTraceFileReader = Files.newBufferedReader(Paths.get(t));
        } catch (IOException e) {
            throw new TraceVisualizerException("Error creating reader for trace file ".concat(t));
        }
    }

    /**
     * Sets the name of the trace stats file which causes a writer to be created.
     * 
     * @param s the trace stats file name
     * @throws TraceVisualizerException if a writer cannot be obtained
     */
    public void setTraceStatsFile(String s) throws TraceVisualizerException {
        try {
            traceStatsFileWriter = Files.newBufferedWriter(Paths.get(s), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TraceVisualizerException("Error creating Stats Trace File ".concat(s));
        }
    }

    /**
     * Sets the name of the visualized trace file which causes a writer to be
     * created.
     * 
     * @param v the visualized trace File name
     * @throws TraceVisualizerException if a writer cannot be obtained
     */
    public void setVisualizedTraceFile(String v) throws TraceVisualizerException {
        try {
            visualizedTraceFileWriter = Files.newBufferedWriter(Paths.get(v), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TraceVisualizerException("Error creating Visualized Trace File ".concat(v));
        }
    }

    /**
     * Set the name of the thread.
     */
    public void setTraceThreadName(String t) {
        traceThreadName = t == null ? DEFAULT_THREAD_NAME : t;
    }

    /**
     * Get the name of the thread.
     */
    public String getTraceThreadName() {
        return traceThreadName;
    }

    /**
     * Process a line in the visualization file by saving the information needed
     * later to construct a useful visualization of the trace. Note that exits are
     * not saved.
     * 
     * @param l the line number in the trace file at which the trace line was found
     * @param t the line of text from the trace file
     */
    protected void processRawTraceLine(int l, String t) {
        if (t.contains("Method entered:")) {
            logger.debug("enter of trace node {}", l);
            String methodName = t.substring(t.indexOf(',') + 1, t.indexOf("line") - 2);
            String lineNumber = t.substring(t.indexOf("line=") + "line=".length(), t.indexOf("bci") - 1);
            TraceNode n = new TraceNode(l, methodName, lineNumber, depth, parent);
            logger.debug("put trace node {} into the trace tree", l);
            traceNodes.put(l, n);
            parent = n;
            depth++;
        } else if (t.contains("Method exited:")) {
            if (parent != null) {
                parent = parent.parent;
            }
            depth--;
        }
    }

    /**
     * Collects stats about a line in the visualization file.
     * 
     * @param l A line of text from the visualization trace file.
     */
    protected void collectTraceStats(String l) {
        if (traceStatsFileWriter != null) {
            logger.debug("Stats collected for line {}.", l);
        }
    }

    /**
     * Prints a generic header.
     * 
     * @throws TraceVisualizerException if an IO error occurs printing the header
     */
    protected void printHeader() throws TraceVisualizerException {
        try {
            visualizedTraceFileWriter.write("------------------------------------------");
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Prints a generic footer.
     * 
     * @throws TraceVisualizerException if an IO error occurs printing the footer
     */
    protected void printFooter() throws TraceVisualizerException {
        try {
            visualizedTraceFileWriter.write("------------------------------------------");
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Prints trace information for a given trace node.
     * 
     * @throws TraceVisualizerException if an IO error occurs writing the line
     */
    protected void printVisualizedTraceNode(TraceNode n) throws TraceVisualizerException {
        try {
            visualizedTraceFileWriter
                    .write(String.format(" printVisualizedTraceNode for %s %s", n.methodName, n.lineNumber));
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Sets the logger.
     * 
     * @param l logger
     */
    protected void setLogger(Logger l) {
        logger = l;
        logger.debug("logger set to {} ", l);
    }
}
