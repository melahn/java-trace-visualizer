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
    TraceNode currentNode = null;
    String rawTraceFilename = null;
    BufferedReader rawTraceFileReader = null;
    Map<Integer, TraceNode> traceNodes = new LinkedHashMap<>();
    Map<String, Integer> traceStats = new HashMap<>();
    String traceStatsFilename = null;
    BufferedWriter traceStatsFileWriter = null;
    String traceThreadName = null;
    String visualizedTraceFilename = null;
    BufferedWriter visualizedTraceFileWriter = null;
    static final int INDENT_INCREMENT = 5;
    static final String DEFAULT_THREAD_NAME = "main";

    /**
     * Constructor that accepts the trace file name, the visualised trace file name,
     * the stats file name and the trace thread name.
     * 
     * @param r the name of the raw trace flile
     * @param v the name of the visualized trace file
     * @param s the name of the stats file (may be null)
     * @param t the name of the trace thread (may be null)
     * 
     * @throws TraceVisualizerException if the constructor fails
     */
    TraceVisualizerBasePrinter(String r, String v, String s, String t) throws TraceVisualizerException {
        setLogger(LogManager.getLogger());
        setRawTraceFile(r);
        setVisualizedTraceFile(v);
        setTraceStatsFile(s);
        setTraceThreadName(t);
        logger.debug("TraceVisualizerBasePrinter created using '{}' '{}' '{}' '{}'", r, v, s, t);
    }

    /**
     * Base Constructor.
     */
    TraceVisualizerBasePrinter() {
        setLogger(LogManager.getLogger());
        logger.info("TraceVisualizerBasePrinter created");
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
        currentNode = new TraceNode(0, "start", "0", depth++, null);
        traceNodes.put(0, currentNode);
        try {
            while (t != null) {
                t = rawTraceFileReader.readLine();
                if (t != null) {
                    processRawTraceLine(i++, t);
                }
            }
            printVisualizedTraceFile();
        } catch (IOException e) {
            throw new TraceVisualizerException("IO Exception reading trace file");
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
     * Prints the collected stats, as a csv file. Each line in the file contains the
     * method name and the number of times the method was called, separated by a
     * comma.
     * 
     * @throws TraceVisualizerException if the stats could not be printed
     */
    public void printTraceStats() throws TraceVisualizerException {
        try {
            int i = 0;
            if (traceStatsFileWriter != null) {
                for (Map.Entry<String, Integer> e : traceStats.entrySet()) {
                    StringBuilder s = new StringBuilder(e.getKey()).append(",").append(e.getValue().toString());
                    if (++i < traceStats.size()) { // avoid a blank at the end of file
                        s.append("\n");
                    }
                    traceStatsFileWriter.write(s.toString());
                }
                traceStatsFileWriter.close();
                logger.debug("Trace Stats Printed");
            }
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Handles the primtImage method by throwing an exception.
     * 
     * @throws TraceVisualizerException to signal that the method is a no-op
     */
    public void printImage() throws TraceVisualizerException {
        logger.debug("{} does not know how to print an image", this.getClass().getName());
        throw new TraceVisualizerException(String.format("%s does not know how to print an image", this.getClass().getName()));
    }

    /**
     * Sets the name of the raw trace file which causes a reader to be created.
     * 
     * @param r the raw trace file name
     * @throws TraceVisualizerException if a reader cannot be obtained
     */
    public void setRawTraceFile(String r) throws TraceVisualizerException {
        rawTraceFilename = r;
        try {
            rawTraceFileReader = Files.newBufferedReader(Paths.get(r));
        } catch (IOException e) {
            throw new TraceVisualizerException("Error creating reader for trace file ".concat(r));
        }
    }

    /**
     * Sets the name of the trace stats file which causes a writer to be created.
     * 
     * @param s the trace stats file name (may be null)
     * @throws TraceVisualizerException if a writer cannot be obtained
     */
    public void setTraceStatsFile(String s) throws TraceVisualizerException {
        traceStatsFilename = s;
        if (s != null) {
            try {
                traceStatsFileWriter = Files.newBufferedWriter(Paths.get(s), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new TraceVisualizerException("Error creating Stats Trace File ".concat(s));
            }
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
        visualizedTraceFilename = v;
        try {
            visualizedTraceFileWriter = Files.newBufferedWriter(Paths.get(v), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TraceVisualizerException("Error creating Visualized Trace File ".concat(v));
        }
    }

    /**
     * Set the name of the thread.
     * 
     * @param t the thread name
     */
    public void setTraceThreadName(String t) {
        traceThreadName = t == null ? DEFAULT_THREAD_NAME : t;
    }

    /**
     * Get the name of the thread.
     * 
     * @return the thread name
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
            TraceNode n = new TraceNode(l, methodName, lineNumber, depth, currentNode);
            logger.debug("put trace node {} into the trace tree", l);
            traceNodes.put(l, n);
            currentNode = n;
            depth++;
            collectTraceStats(methodName);
        } else if (t.contains("Method exited:")) {
            currentNode = currentNode.parent;
            depth--;
        }
    }

    /**
     * Updates the number of calls on a method.
     * 
     * @param m A method name
     * @return the number of calls to m, so far collected
     */
    protected Integer collectTraceStats(String m) {
        logger.debug("collect stats for {}", m);
        return traceStats.get(m) == null ? traceStats.put(m, 1) : traceStats.replace(m, traceStats.get(m) + 1);
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
     * @param n the trace node to print
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
    public void setLogger(Logger l) {
        logger = l;
        logger.debug("logger set to {} ", l);
    }

}
