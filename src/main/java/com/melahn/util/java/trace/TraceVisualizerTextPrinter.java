package com.melahn.util.java.trace;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TraceVisualizerTextPrinter extends TraceVisualizerBasePrinter implements TraceVisualizerPrinter {

    /**
     * Constructor with stats file name.
     * 
     * @throws TraceVisualizerException when an error occurs from super
     */
    TraceVisualizerTextPrinter(String r, String v, String s, String t) throws TraceVisualizerException {
        super(r, v, s, t);
    }

    /**
     * Constructor without stats file name.
     * 
     * @throws TraceVisualizerException when an error occurs from super
     */
    TraceVisualizerTextPrinter(String r, String v, String t) throws TraceVisualizerException {
        super(r, v, t);
    }

    /**
     * Print a header consisting of just an indicator of the thread.
     * 
     * @throws TraceVisualizerException when an IO error occurs printing the header
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
     * @throws TraceVisualizerException when an IO error occurs printing the footer
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
     * @throws TraceVisualizerException when an IO error occurs printing the node
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
     * Note that I don't return a TraceVisualizerException here as I do in most other
     * methods because this makes it possible to cover the test of the IO Exception 
     * in the caller instead of here, which would otherwise require a getter to obtain
     * the writer.  A small optimization.
     * 
     * @param n the node the relevent vertices of which are to be printed
     * @throws IOException when an IO Exception occurs writing the vertices
     */
    protected void printVertices(TraceNode n) throws IOException {
        char[] c = new char[INDENT_INCREMENT * n.depth];
        for (int i = 0; i < c.length; i++) {
            c[i] = 32;
        }
        List<TraceNode> a = n.getUnprintedAncestors();
        for (TraceNode t : a) {
            c[INDENT_INCREMENT * t.depth - 1] = 124;
        }
        String s = new String(c);
        visualizedTraceFileWriter.write(s);
        visualizedTraceFileWriter.write("\n");
        visualizedTraceFileWriter.write(s);
    }
}
