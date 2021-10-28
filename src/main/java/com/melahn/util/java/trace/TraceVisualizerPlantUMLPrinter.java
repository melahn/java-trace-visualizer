package com.melahn.util.java.trace;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            String h = "@startuml trace\nsalt\n{\n{T\n";
            visualizedTraceFileWriter.write(h);
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
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            StringBuilder s = new StringBuilder();
            s.append("\n\ncenter footer Generated on ").append(f.format(LocalDateTime.now())).append(" by ")
                    .append(this.getClass().getCanonicalName())
                    .append("(https://github.com/melahn/java-trace-visualizer)").append("\n}\n}\n@enduml");
            visualizedTraceFileWriter.write(s.toString());
            visualizedTraceFileWriter.close();
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
            StringBuilder b = new StringBuilder(" ");
            for (int i = 0; i < n.depth; i++) {
                b.append("+");
            }
            b.append(n.methodName).append("|").append(n.lineNumber).append("\n");
            visualizedTraceFileWriter.write(b.toString());
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }
}
