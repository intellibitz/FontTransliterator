package sted.fontmap;

import sted.event.ThreadEventSourceBase;
import sted.event.TransliterateEvent;
import sted.util.FileHelper;

import java.io.*;
import java.util.logging.Logger;

public class Converter
        extends ThreadEventSourceBase {
    private boolean stopRequested;
    private FontMap fontMap;
    private File fileToConvert;
    private File convertedFile;
    private boolean initialized;
    private boolean success;
    private static final Logger logger =
            Logger.getLogger("sted.fontmap.Converter");
    private ITransliterate transliterate;

    public Converter() {
        super();
        setThreadEvent(new TransliterateEvent(this));
        initTransliterator();
    }

    public Converter(FontMap fontMap, File input, File output) {
        this();
        init(fontMap, input, output);
    }

    private void init(FontMap fontMap, File input, File output) {
        fileToConvert = input;
        convertedFile = output;
        initialized = true;
        success = false;
        stopRequested = false;
        setFontMap(fontMap);
    }

    private void initTransliterator() {
        if (transliterate == null) {
            transliterate = new DefaultTransliterator();
        }
    }

    public ITransliterate getTransliterate() {
        return transliterate;
    }

    public void setTransliterate(ITransliterate transliterate) {
        this.transliterate = transliterate;
    }

    public void run() {
        fireThreadRunStarted();
        if (!initialized) {
            throw new IllegalThreadStateException(
                    "Thread should be initialized.. Call init method before invoking run");
        }
        convertFile();
        initialized = false;
        if (success) {
            fireThreadRunFinished();
        } else {
            fireThreadRunFailed();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    boolean isReady() {
        return fontMap != null && !fontMap.getEntries().isEmpty();
    }

    public void setHTMLAware(boolean flag) {
        transliterate.setHTMLAware(flag);
    }

    private void convertFile() {
        try {
            FileHelper.fileCopy(fileToConvert,
                    fileToConvert.getAbsolutePath() + ".bakup");
        } catch (IOException e) {
            setMessage("Unable to Backup input file: " + e.getMessage());
            success = false;
            logger.severe("Unable to Backup input file: " + e.getMessage());
            logger.throwing(getClass().getName(), "convertFile", e);
            return;
        }
        final BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileToConvert));
        } catch (FileNotFoundException e) {
            setMessage("File Not Found: " + e.getMessage());
            success = false;
            logger.severe("Cannot Read - File Not Found: " + e.getMessage());
            logger.throwing(getClass().getName(), "convertFile", e);
            return;
        }
        final BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(convertedFile));
        } catch (IOException e) {
            setMessage("Cannot create Writer: " + e.getMessage());
            success = false;
            logger.severe("Cannot Write - IOException: " + e.getMessage());
            logger.throwing(getClass().getName(), "convertFile", e);
            return;
        }
        String input;
        try {
            while ((input = bufferedReader.readLine()) != null) {
                if (stopRequested) {
                    break;
                }
                bufferedWriter.write(transliterate.parseLine(input));
                bufferedWriter.newLine();
                fireThreadRunning();
            }
        } catch (IOException e) {
            setMessage("IOException: " + e.getMessage());
            success = false;
            logger.severe(
                    "IOException - Ceasing Conversion: " + e.getMessage());
            logger.throwing(getClass().getName(), "convertFile", e);
            return;
        } finally {
            try {
                bufferedReader.close();
                bufferedWriter.close();
            } catch (IOException e) {
                setMessage("Cannot Close Reader/Writer - IOException: " +
                        e.getMessage());
                success = false;
                logger.severe(
                        "Cannot close File Streams - Ceasing Conversion: " +
                                e.getMessage());
                logger.throwing(getClass().getName(), "convertFile", e);
                return;
            }
        }
        if (!stopRequested) {
            setMessage("Transliterate Done.");
            success = true;
        }
    }

    public void setReverseTransliterate(boolean flag) {
        transliterate.setReverseTransliterate(flag);
    }

    public void setFontMap(FontMap fontMap) {
        this.fontMap = fontMap;
        if (fontMap != null) {
            transliterate.setEntries(fontMap.getEntries());
        }
    }

    public synchronized void setStopRequested(boolean flag) {
        stopRequested = flag;
    }
}

