package com.melahn.util.java.trace;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TraceVisualizer {

    private boolean generateImage = false;
    private String inputFilename = null;
    private static Logger logger = LogManager.getLogger(TraceVisualizer.class.getName());
    private String outputFilename = null;
    private String statsFilename = null;

    TraceVisualizer() {
        /* Nothing to do here */
    }

    public static void main(String[] a) {
        try {
            TraceVisualizer tv = new TraceVisualizer();
            if (tv.parseArgs(a)) {
                TraceVisualizerPrinter tvp = tv.outputFilename.endsWith(".puml")? new TraceVisualizerPlantUMLPrinter(tv.inputFilename, tv.outputFilename, tv.statsFilename, null)
                            : new TraceVisualizerTextPrinter(tv.inputFilename, tv.outputFilename, tv.statsFilename, null);
                tvp.processRawTraceFile();
                tvp.printTraceStats();
                if (tv.generateImage) {
                    tvp.printImage();
                }
            }
        } catch (TraceVisualizerException e) {
            logger.error("A TraceVisualizerException occured: ".concat(e.getLocalizedMessage()));
        }
    }

    /**
     * Gets Help.
     * 
     * @return the help that the user would see on the command line
     */
    private String getHelp() {
        return "\nUsage:\n\n".concat("java -jar java-trace-visualizer-1.0.0-SNAPSHOT.jar\n").concat("\nFlags:\n")
                .concat("\t-i\t<filename>\tThe input file (a trace file created from jdb)\n")
                .concat("\t-o\t<filename>\tThe output file (a visualization file created by this program)\n")
                .concat("\t-s\t<filename>\tThe stats file (a csv file created by this program)\n")
                .concat("\t-g\t\t\tGenerate image from PlantUML file\n").concat("\t-h\t\t\tHelp for this program\n")
                .concat("\nSee https://github.com/melahn/java-trace-visualizer for more information\n");
    }

    /**
     * Parse the command line args.
     * 
     * @param a command line args
     * @return boolean true if processing should continue, false otherwise
     * @throws TraceVisualizerException should a parse error occur
     */
    private boolean parseArgs(String[] a) throws TraceVisualizerException {
        Options o = setOptions();
        try {
            CommandLine c = new DefaultParser().parse(o, a);
            parseOptions(c);
            parseSwitches(c);
            if (a.length == 0 || c.hasOption("h")) {
                logger.info(getHelp());
                return false;
            }
            if (inputFilename == null || outputFilename == null) {
                throw new ParseException("Both the -i and -o options must be specified");
            }
            return true;
        } catch (ParseException e) {
            logger.error(e.getMessage());
            throw new TraceVisualizerException(String.format("Parse Exception: %s", e.getMessage()));
        }
    }

    /**
     * Parse the options from the command line.
     * 
     * @param c the command line
     * @return a count of the options found
     */
    private int parseOptions(CommandLine c) {
        int i = 0;
        if (c.hasOption("i")) {
            setInputFilename(c.getOptionValue("i"));
            i++;
        }
        if (c.hasOption("o")) {
            setOutputFilename(c.getOptionValue("o"));
            i++;
        }
        if (c.hasOption("s")) {
            setStatsFilename(c.getOptionValue("s"));
            i++;
        }
        return i;
    }

    /**
     * Parse the switches from the command line.
     * 
     * @param c the command line
     */
    private void parseSwitches(CommandLine c) {
        if (c.hasOption("g")) {
            setGenerateImage(true);
        }
    }

    /**
     * Sets the Command line options in anticipation of parsing.
     * 
     * @returns the Options
     */
    private Options setOptions() {
        Options options = new Options();
        options.addOption("i", true, "The input file name");
        options.addOption("o", true, "The output file name");
        options.addOption("s", true, "The stats file name");
        options.addOption("g", false, "Generate Image from PlantUML file");
        options.addOption("h", false, "Help");
        return options;
    }

    /**
     * Set the image generate option.
     * 
     * @param b a boolean representing whether the user wanted to 
     * generate the image from a PlantUML file
     */
    public void setGenerateImage(boolean b) {
        generateImage = b;
    }

    /**
     * Get the image generate option.
     * 
     * @return a boolean representing whether the user wanted to 
     * generate the image from a PlantUML file
     */
    public boolean getGenerateImage() {
        return generateImage;
    }

    /**
     * Set the input trace file name.
     * 
     * @param f the input trace file name
     */
    public void setInputFilename(String f) {
        inputFilename = f;
    }

    /**
     * Get the input trace file name.
     * 
     * @return the input trace file name
     */
    public String getInputFilename() {
        return inputFilename;
    }

    /**
     * Set the output visualized trace file name.
     * 
     * @param f visualized trace file name
     */
    public void setOutputFilename(String f) {
        outputFilename = f;
    }

     /**
     * Get the output visualized trace file name.
     * 
     * @return the output visualized trace file name
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     * Set the stats file name.
     * 
     * @param f stats file name
     */
    public void setStatsFilename(String f) {
        statsFilename = f;
    }

    /**
     * Get the stats file name.
     * 
     * @return the stats file name
     */
    public String getStatsFilename() {
        return statsFilename;
    }
}
