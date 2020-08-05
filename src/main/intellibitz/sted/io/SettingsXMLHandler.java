package intellibitz.sted.io;

import intellibitz.sted.util.FileHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Reads the Settings XML file
 */
public class SettingsXMLHandler
        extends DefaultHandler {
    private File stedSettingsFile;
    private final Map<String, String> settings = new HashMap<String, String>();
    private String key;
    private String value;
    private static final Logger logger =
            Logger.getLogger("intellibitz.sted.io.SettingsXMLHandler");

    public SettingsXMLHandler() {
        super();
    }

    public SettingsXMLHandler(File stedSettings) {
        this();
        stedSettingsFile = stedSettings;
    }

    public void read()
            throws IOException, ParserConfigurationException, SAXException {
        logger.entering(getClass().getName(), "read", stedSettingsFile);
        final SAXParserFactory saxParserFactory =
                SAXParserFactory.newInstance();
        saxParserFactory.setValidating(true);
        final SAXParser saxParser = saxParserFactory.newSAXParser();
        logger.info("reading settings file - " +
                stedSettingsFile.getAbsolutePath());
        final InputStream inputStream =
                FileHelper.getInputStream(stedSettingsFile);
        if (inputStream == null) {
            logger.severe(
                    "InputStream NULL - Cannot Read File: " + stedSettingsFile);
            throw new IOException(
                    "InputStream NULL - Cannot Read File: " + stedSettingsFile);
        } else {
            saxParser.parse(inputStream, this);
        }
        logger.exiting(getClass().getName(), "read");
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes)
            throws SAXException {
        if ("settings".equals(qName)) {
        } else if ("option".equals(qName)) {
            key = attributes.getValue("name");
            value = attributes.getValue("value");
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if ("option".equals(qName)) {
            if (key != null) {
                settings.put(key, value);
            }
        }
    }

    public Map<String, String> getSettings() {
        return settings;
    }
}
