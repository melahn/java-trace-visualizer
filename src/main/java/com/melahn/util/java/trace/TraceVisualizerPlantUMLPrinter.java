package com.melahn.util.java.trace;

import java.io.IOException;

public class TraceVisualizerPlantUMLPrinter extends TraceVisualizerBasePrinter implements TraceVisualizerPrinter {
    
    /**
     * Constructor.
     * 
     * @throws TraceVisualizerException
     */
    TraceVisualizerPlantUMLPrinter(String r, String v, String s, String t) throws TraceVisualizerException {
        super(r, v, s, t);
    }

     /**
     * Print a PlantUML header.
     * 
     * @throws TraceVisualizerException when an IO error occurs printing the header
     */
    @Override
    public void printHeader() throws TraceVisualizerException {
        try {
            visualizedTraceFileWriter.write("PlantUML Header"); // placeholder
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

     /**
     * Print a PlantUML footer.
     * 
     * @throws TraceVisualizerException when an IO error occurs printing the footer
     */
    @Override
    public void printFooter() throws TraceVisualizerException {
        try {
            visualizedTraceFileWriter.write("PlantUML Footer"); // placeholder
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Print a visualation of the trace node in PlantUML form.  
     * 
     * @throws TraceVisualizerException when an IO error occurs printing the node
     */
    @Override
    public void printVisualizedTraceNode(TraceNode n) throws TraceVisualizerException {
        try {
            visualizedTraceFileWriter.write(String.format("printVisualizedTraceNode %s %s", n.methodName, n.lineNumber)); // placeholder
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }
}
