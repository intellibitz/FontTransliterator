package sted;

import sted.event.IThreadListener;
import sted.event.ThreadEvent;
import sted.fontmap.Converter;
import sted.fontmap.FontMap;
import sted.io.FontMapReader;
import sted.util.Resources;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public class STEDConsole
        implements IThreadListener {
    private String fontMapName;
    private String inputFileName;
    private String outputFileName;
    private boolean reverse;
    private boolean html;
    private static Logger logger;

    public STEDConsole(List<String> args) {
        logger = Logger.getLogger("sted.STEDConsole");
        Main.getLogmanager().addLogger(logger);
        loadArgs(args);
        if (null == fontMapName || Resources.EMPTY_STRING.equals(fontMapName)) {
            logger.info("Invalid FontMap: " + fontMapName);
            printUsage();
            System.exit(1);
        }
        if (null == inputFileName ||
                Resources.EMPTY_STRING.equals(inputFileName)) {
            logger.info("Invalid Input File: " + inputFileName);
            printUsage();
            System.exit(2);
        }
        if (null == outputFileName ||
                Resources.EMPTY_STRING.equals(outputFileName)) {
            logger.info("Invalid Output File: " + outputFileName);
            printUsage();
            System.exit(3);
        }
        final File input = new File(inputFileName);
        final File output = new File(outputFileName);
        // create console based FontMap.. no need to readFontMap fonts
        final FontMap fontMap = new FontMap(new File(fontMapName), true);
        try {
            FontMapReader.read(fontMap);
            final Converter converter = new Converter(fontMap, input, output);
            converter.setReverseTransliterate(reverse);
            converter.setHTMLAware(html);
            converter.addThreadListener(this);
            logger.info("Running Transliterator with the following options: ");
            logger.info("   FontMap: " + fontMapName);
            logger.info("   Input: " + inputFileName);
            logger.info("   Output: " + outputFileName);
            logger.info("   Preserve Tags in Transliteration: " + html);
            logger.info("   Reverse Transliteration: " + reverse);
            converter.start();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            logger.severe("Exception : " + e.getMessage());
            logger.throwing("sted.STEDConsole",
                    "Constructor", e);
            System.exit(-1);
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            logger.severe("Exception : " + e.getMessage());
            logger.throwing("sted.STEDConsole",
                    "Constructor", e);
            System.exit(-1);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            logger.severe("Exception : " + e.getMessage());
            logger.throwing("sted.STEDConsole",
                    "Constructor", e);
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        try {
            new STEDConsole(Arrays.asList(args));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            logger.severe("Exception : " + e.getMessage());
            logger.throwing("sted.STEDConsole", "main", e);
            System.exit(-1);
        } finally {
        }
    }

    private static void printUsage() {
        logger.info("STED Console Usage: ");
        logger.info(
                "   java -Dfontmap.file='<file>' -Dinput.file='<input>' -Doutput.file='<output>' sted.STEDConsole");
        logger.info(" -OR- ");
        logger.info(
                "   java sted.STEDConsole -map='<file>' -in='<input>' -out='<output>'");
    }

    private void loadArgs(List<String> args) {
        for (final String param : args) {
            if (param.startsWith("-map=")) {
                fontMapName = param.substring(5);
            } else if (param.startsWith("-in=")) {
                inputFileName = param.substring(4);
            } else if (param.startsWith("-out=")) {
                outputFileName = param.substring(5);
            }
        }
        reverse = args.contains("-r") || args.contains("-R");
        html = args.contains("-p") || args.contains("-P");
        if (fontMapName == null) {
            fontMapName = System.getProperty(Resources.FONTMAP_FILE);
        }
        if (inputFileName == null) {
            inputFileName = System.getProperty(Resources.INPUT_FILE);
        }
        if (outputFileName == null) {
            outputFileName = System.getProperty(Resources.OUTPUT_FILE);
        }
    }

    public void threadRunStarted(ThreadEvent e) {

    }

    public void threadRunning(ThreadEvent e) {

    }

    public void threadRunFailed(ThreadEvent e) {
        logger.severe(e.getEventSource().getMessage().toString());
        System.exit(-1);
    }

    public void threadRunFinished(ThreadEvent e) {
        logger.info(e.getEventSource().getMessage().toString());
        System.exit(0);
    }
}
