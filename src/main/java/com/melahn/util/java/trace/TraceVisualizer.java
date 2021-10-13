package com.melahn.util.java.trace;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TraceVisualizer {

    private static boolean generateImage = false;
    protected static String inputFilename = null;
    private static String outputFilename = null;

    public static void main (String[] a) {
        Logger logger = LogManager.getLogger(TraceVisualizer.class.getName());
        try {
            parseArgs(a);
        }
        catch (TraceVisualizerException e) {
            logger.error("A TraceVisualizerException occured: ".concat(e.getLocalizedMessage()));
        }
    }
    /**
     * Gets Help.
     */
    public static String getHelp() {
        return "\nUsage:\n\n".concat("java -jar java-trace-visualizer-1.0.0-SNAPSHOT.jar\n").concat("\nFlags:\n")
                .concat("\t-i\t<filename>\tThe trace file created from jdb\n")
                .concat("\t-o\t<filename>\tThe visualization created by this program\n")
                .concat("\t-g\t\t\tGenerate image from PlantUML file\n")
                .concat("\t-h\t\t\tHelp for this program\n")
                .concat("\nSee https://github.com/melahn/java-trace-visualizer for more information\n");
    }

    /**
     * Parse the command line args.
     * 
     * @param a command line args
     * @return boolean true if processing should continue, false otherwise
     * @throws TraceVisualizerException should a parse error occur
     */
    protected static boolean parseArgs(String[] a) throws TraceVisualizerException {
        Options o = setOptions();  
        try {
            CommandLine c = new DefaultParser().parse(o, a);
            int i = parseOptions(c);
            parseSwitches(c);
            if (a.length == 0 || c.hasOption("h")) {
                LogManager.getLogger().info(TraceVisualizer.getHelp());
                return false;
            }
            if (i != 2) {
                throw new ParseException("Both the -i and -o options must be specified");
            }
            return true;
        } catch (ParseException e) {
            LogManager.getLogger().error(e.getMessage());
            throw new TraceVisualizerException(String.format("Parse Exception: %s", e.getMessage()));
        }
    }

    /**
     * Parse the options from the command line.
     * 
     * @param c the command line
     * @return a count of the options found
     */
    private static int parseOptions(CommandLine c) {
        int i = 0; 
        if (c.hasOption("i")) { 
            setInputFilename(c.getOptionValue("f"));
            i++;
        }
        if (c.hasOption("o")) {
            setOutputFilename(c.getOptionValue("o"));
            i++;
        }
        return i;
    }

    /**
     * Parse the switches from the command line.
     * 
     * @param c the command line
     */
    private static void parseSwitches(CommandLine c) {
        if (c.hasOption("g")) {
            setGenerateImage(true);
        }
    }

    /**
     * Sets the Command line options in anticipation of parsing.
     * 
     * @returns the Options
     */
    private static Options setOptions() {
        Options options = new Options();
        options.addOption("i", true, "The input file name");
        options.addOption("o", true, "The output file name");
        options.addOption("g", false, "Generate Image from PlantUML file");
        options.addOption("h", false, "Help");
        return options;
    }

    /**
     * Getters and Setters
     */

    public static void setGenerateImage(boolean b) {
        generateImage = b;
    }
    public static boolean getGenerateImage() {
        return generateImage;
    }
    public static void setInputFilename(String f) {
        inputFilename = f;
    }
    public static String getInputFilename() {
        return inputFilename;
    }
    public static void setOutputFilename(String f) {
        outputFilename = f;
    }
    public static String getOutputFilename() {
        return outputFilename;
    }
}
