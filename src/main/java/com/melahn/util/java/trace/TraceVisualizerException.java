package com.melahn.util.java.trace;

import java.util.UUID;

/*
 *  TraceVisualizer is an exception class designed for use by the Trace Visualizer.
*/

public class TraceVisualizerException extends Exception { 
    static final long serialVersionUID = UUID.fromString("5a8dba66-71e1-492c-bf3b-53cceb67b785").getLeastSignificantBits();

    public TraceVisualizerException(String message) {
        super(message);
    }
}

