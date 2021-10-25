package com.melahn.util.java.trace;

public interface TraceVisualizerPrinter {

    public void printHeader() throws TraceVisualizerException;

    public void printFooter() throws TraceVisualizerException;

    public void printTraceStats() throws TraceVisualizerException;

    public void printVisualizedTraceNode(TraceNode n) throws TraceVisualizerException;

    public void processRawTraceFile() throws TraceVisualizerException;
    
}
