package com.melahn.util.java.trace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TraceVisualizerPlantUMLPrinter extends TraceVisualizerBasePrinter implements TraceVisualizerPrinter {

    private static final String INDENT = "    ";

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
            StringBuilder h = new StringBuilder("@startuml Java Trace\n");
            h.append("scale 2\n");
            h.append("skinparam FooterFontColor Gray\n");
            h.append("skinparam FooterFontSize 6\n");
            h.append("salt\n{\n").append(INDENT).append("{T\n");
            h.append(INDENT).append(INDENT).append(" Method Call").append(" | ").append(" Line Number\n");
            visualizedTraceFileWriter.write(h.toString());
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
            s.append(INDENT).append("}\n").append("}\n").append("center footer Generated on ")
                    .append(f.format(LocalDateTime.now())).append(" by ").append(this.getClass().getCanonicalName())
                    .append("(https://github.com/melahn/java-trace-visualizer)").append("\n@enduml");
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
            StringBuilder b = new StringBuilder(INDENT).append(INDENT);
            for (int i = 0; i < n.depth; i++) {
                b.append("+");
            }
            b.append(n.methodName).append(" | ").append("<color:Gray>").append(n.lineNumber).append("\n");
            visualizedTraceFileWriter.write(b.toString());
        } catch (IOException e) {
            throw new TraceVisualizerException(e.getMessage());
        }
    }

    /**
     * Generates an image from a PUML file
     * 
     * @param f the name of the Plant UML file
     * @throws TraceVisualizerException if an error occurred generaing the image
     */
    @Override
    public void printImage(String f) throws TraceVisualizerException {
        try {
            net.sourceforge.plantuml.SourceFileReader r = new net.sourceforge.plantuml.SourceFileReader(Paths.get(f).toAbsolutePath().toFile());
            if (!r.hasError()) {
                List<net.sourceforge.plantuml.GeneratedImage> l = r.getGeneratedImages();
                if (!l.isEmpty()) {
                    Path s = Paths.get(l.get(0).getPngFile().toString());
                    Path t = Paths.get(Paths.get(f).toAbsolutePath().toString().replace("puml", "png"));
                    Files.move(s, t);
                    logger.debug("Image file {} generated from {}", t, s);
                } else {
                    logger.debug("Warning: Image file was not generated from {}", f);
                }
            } else {
                logger.error("Error in net.sourceforge.plantuml.GeneratedImage trying to generate image from {}", f);
            }
        } catch (IOException e) {
            logger.error("IOException in net.sourceforge.plantuml.GeneratedImage trying to generate image from {}", f);
            throw new TraceVisualizerException(e.getMessage());
        }
    }
}
