package sted.io;

import sted.event.FontMapReadEvent;
import sted.event.ThreadEventSourceBase;
import sted.fontmap.FontMap;
import sted.fontmap.FontMapEntry;
import sted.util.FileHelper;
import sted.util.Resources;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class FontMapReader
        extends ThreadEventSourceBase {
    private static final String FONT1_TITLE = "FONT1";
    private static final String FONT2_TITLE = "FONT2";
    private static final String HEADER = "FONT MAPPER 1.0";
    private static final String HEADER_TITLE = "HEADER";
    private static final String INT_DELIMITER = ":";
    private static final String PROPERTY_DELIMITER = "=";
    private static final String STARTS_WITH = "STARTS";
    private static final String ENDS_WITH = "ENDS";
    private static final String FOLLOWED_BY = "FBY";
    private static final String PRECEDED_BY = "PBY";
    private static final String COMMA = ",";

    private FontMap fontMap;
    private static final Logger logger =
            Logger.getLogger("sted.io.FontMapReader");

    //
    public FontMapReader(FontMap fontMap) {
        super();
        final File file = fontMap.getFontMapFile();
        if (null == file) {
            throw new IllegalArgumentException("Cannot Load - File is null");
        }
        this.fontMap = fontMap;
        // create a FontMapReadEvent.. Identifies the Event with FontMapReader
        setThreadEvent(new FontMapReadEvent(this));
    }

    /**
     * If this thread was constructed using a separate <code>Runnable</code> run
     * object, then that <code>Runnable</code> object's <code>run</code> method
     * is called; otherwise, this method does nothing and returns.
     * <p/>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see Thread#start()
     * @see Thread#stop()
     * @see Thread#Thread(ThreadGroup, Runnable, String)
     * @see Runnable#run()
     */
    public void run() {
        fireThreadRunStarted();
        try {
            read(fontMap);
            fireThreadRunFinished();
        } catch (IOException e) {
            setMessage("Invalid FontMap " +
                    fontMap.getFontMapFile().getAbsolutePath());
            logger.throwing("sted.actions.LoadFontMapAction",
                    "readFontMap", e);
            fireThreadRunFailed();
        } catch (SAXException e) {
            setMessage("Invalid FontMap " +
                    fontMap.getFontMapFile().getAbsolutePath());
            logger.throwing("sted.actions.LoadFontMapAction",
                    "readFontMap", e);
            fireThreadRunFailed();
        } catch (ParserConfigurationException e) {
            setMessage("Invalid FontMap " +
                    fontMap.getFontMapFile().getAbsolutePath());
            logger.throwing("sted.actions.LoadFontMapAction",
                    "readFontMap", e);
            fireThreadRunFailed();
        } catch (HeadlessException e) {
            setMessage("Invalid FontMap " +
                    fontMap.getFontMapFile().getAbsolutePath());
            logger.throwing("sted.actions.LoadFontMapAction",
                    "readFontMap", e);
            fireThreadRunFailed();
        }
    }

    public static void read(FontMap fontMap)
            throws IOException, SAXException, ParserConfigurationException {
        final File file = fontMap.getFontMapFile();
        if (file.getName().toLowerCase().endsWith(Resources.XML)) {
            new FontMapXMLHandler().read(fontMap);
        } else {
            readOldFormat(fontMap);
        }
    }

    private static void readOldFormat(FontMap fontMap)
            throws IOException {
        final File file = fontMap.getFontMapFile();
        final BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader
                        (FileHelper.getInputStream(file)));
        String input;
        boolean headerFound = false;
        while ((input = bufferedReader.readLine()) != null) {
            if (input.length() > 0 && !(input.startsWith("#") ||
                    input.startsWith("//") ||
                    input.startsWith("/*"))) {
                if (input.indexOf(COMMA) != -1) {
                    fontMap.getEntries().add(createFontMapEntry(input));
                    continue;
                }
                final StringTokenizer stringTokenizer = new StringTokenizer(
                        input.trim(), PROPERTY_DELIMITER, false);
                if (stringTokenizer.countTokens() < 2) {
                    throw new IOException(
                            file + " invalid fontmap - no header found");
                }
                final String token1 = stringTokenizer.nextToken();
                final String token2 = stringTokenizer.nextToken();
                // first line must always be a header
                if (!headerFound && HEADER_TITLE.equals(token1)) {
                    if (HEADER.equals(token2)) {
                        headerFound = true;
                        continue;
                    }
                }
                // if header is not found yet, then first line is not an header -- quit!
                if (!headerFound) {
                    throw new IOException(file +
                            " invalid fontmap - no header found - quitting");
                }
                if (FONT1_TITLE.equals(token1)) {
                    fontMap.setFont1(token2);
                } else if (FONT2_TITLE.equals(token1)) {
                    fontMap.setFont2(token2);
                } else {
                    fontMap.getEntries().add(new FontMapEntry(token1, token2));
                }
            }
        }
    }

    private static FontMapEntry createFontMapEntry(String value) {
        FontMapEntry entry = null;
        final StringTokenizer stringTokenizer =
                new StringTokenizer(value, INT_DELIMITER);
        if (stringTokenizer.countTokens() == 2) {
            entry = new FontMapEntry();
            final String key = stringTokenizer.nextToken();
            StringTokenizer st = new StringTokenizer(key, PROPERTY_DELIMITER);
            if (st.countTokens() == 2) {
                entry.setFrom(st.nextToken());
                entry.setTo(st.nextToken());
            }
            st = new StringTokenizer(stringTokenizer.nextToken(), COMMA);
            while (st.hasMoreTokens()) {
                final StringTokenizer st2 =
                        new StringTokenizer(st.nextToken(), PROPERTY_DELIMITER);
                if (st2.countTokens() == 2) {
                    final String ruleprop = st2.nextToken();
                    if (STARTS_WITH.equals(ruleprop)) {
                        entry.setBeginsWith(Boolean.valueOf(st2.nextToken()));
                    } else if (ENDS_WITH.equals(ruleprop)) {
                        entry.setEndsWith(Boolean.valueOf(st2.nextToken()));
                    } else if (FOLLOWED_BY.equals(ruleprop)) {
                        entry.setFollowedBy(st2.nextToken());
                    } else if (PRECEDED_BY.equals(ruleprop)) {
                        entry.setPrecededBy(st2.nextToken());
                    }
                }
            }
        }
        return entry;
    }

}
