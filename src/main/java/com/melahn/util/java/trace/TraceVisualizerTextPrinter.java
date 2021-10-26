package com.melahn.util.java.trace;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TraceVisualizerTextPrinter extends TraceVisualizerBasePrinter implements TraceVisualizerPrinter {

    /**
     * Constructor with stats file name.
     * 
     * @throws TraceVisualizerException
     */
    TraceVisualizerTextPrinter(String r, String v, String s, String t) throws TraceVisualizerException {
        super(r, v, s, t);
    }

    /**
     * Constructor without stats file name.
     * 
     * @throws TraceVisualizerException
     */
    TraceVisualizerTextPrinter(String r, String v, String t) throws TraceVisualizerException {
        super(r, v, t);
    }

    /**
     * Print a header consisting of just an indicator of the thread.
     * 
     * @throws TraceVisualizerException
     */
    @Override
    public void printHeader() throws TraceVisualizerException {
        logger.debug("Print Text Header");
        try {
            visualizedTraceFileWriter.write(String.format(" thread: %s %n", traceThreadName));
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Print a footer consisting of a time stamp and a pointer to the github
     * project.
     * 
     * @throws TraceVisualizerException
     */
    @Override
    public void printFooter() throws TraceVisualizerException {
        logger.debug("Print Text Footer");
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            visualizedTraceFileWriter.write("\nGenerated on " + f.format(LocalDateTime.now()) + " by "
                    + this.getClass().getCanonicalName() + " (https://github.com/melahn/java-trace-visualizer)");
            visualizedTraceFileWriter.close();
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Print a visualation of the trace node in text form. And record that it has been
     * printed so that vertices of later trace nodes can be printed properly computed.
     * 
     * @throws TraceVisualizerException
     */
    @Override
    public void printVisualizedTraceNode(TraceNode n) throws TraceVisualizerException {
        try {
            printVertices(n);
            if (n.depth != 1) {
                visualizedTraceFileWriter.write("____");
            } else {
                visualizedTraceFileWriter.write("  "); // the root node is treated a little differently
            }
            if (n.id != 0) {
                visualizedTraceFileWriter.write(String.format("%s (line %s) %n", n.methodName, n.lineNumber));
            } else {
                visualizedTraceFileWriter.write(String.format("%s %n", n.methodName));
            }
            n.isPrinted = true;
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Prints vertices for a given node. There are two lines printed each containing
     * the vertices of any parent functions that are still 'open' (meaning they have
     * unprinted children).
     * 
     * @throws TraceVisualizerException
     */
    protected void printVertices(TraceNode n) throws TraceVisualizerException {
        char[] c = new char[INDENT_INCREMENT * n.depth];
        for (int i = 0; i < c.length; i++) {
            c[i] = 32;
        }
        List<TraceNode> a = n.getUnprintedAncestors();
        for (TraceNode t : a) {
            c[INDENT_INCREMENT * t.depth - 1] = 124;
        }
        String s = new String(c);
        try {
            visualizedTraceFileWriter.write(s);
            visualizedTraceFileWriter.write("\n");
            visualizedTraceFileWriter.write(s);
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }
}
