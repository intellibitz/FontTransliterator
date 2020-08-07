package sted.io;

import sted.ui.STEDWindow;
import sted.util.MenuHandler;
import sted.util.Resources;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

public class SettingsXMLWriter {
    private SettingsXMLWriter() {
    }

    public static void writeUserSettings(STEDWindow stedWindow)
            throws TransformerException {
        final File file = new File(Resources.SETTINGS_FILE_PATH_USER);
        // Use the parser as a SAX source for input
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(file.getName());
        stringBuffer.append(Resources.NEWLINE_DELIMITER);
        stringBuffer.append(Resources.getVersion());
        stringBuffer.append(Resources.NEWLINE_DELIMITER);
        // write all the options that were customizable by the user
        stringBuffer.append(MenuHandler.getUserOptions());
        write(file, stringBuffer.toString());
    }

    private static void write(File file, String fileContents)
            throws TransformerException {
        // Use a Transformer for output
        final Transformer transformer =
                TransformerFactory.newInstance().newTransformer();
        final SAXSource source = new SAXSource(new SettingsXMLReader(),
                new InputSource(
                        new BufferedReader(
                                new StringReader(
                                        fileContents))));
        transformer.transform(source, new StreamResult(file));
    }

    /**
     * FontMapXMLReader reads the FontMap into an xml document format generates
     * sax events that can be used to transform fontmap to xml document
     */
    static class SettingsXMLReader
            implements XMLReader {
        private ContentHandler contentHandler;
        // We're not doing namespaces, and we have no    // attributes on our elements.
        private final String nsu = Resources.EMPTY_STRING;  // NamespaceURI
        private final String indent = "\n"; // for readability

        SettingsXMLReader() {
        }

        /**
         * @param input
         * @throws java.io.IOException
         * @throws org.xml.sax.SAXException
         */
        public void parse(InputSource input)
                throws IOException, SAXException {
            if (contentHandler == null) {
                throw new SAXException("No content contentHandler");
            }
            final String rootElement = "settings";
            // Get an efficient reader for the file
            final BufferedReader bufferedReader =
                    new BufferedReader(input.getCharacterStream());
            // Read the file and display it's contents.
            contentHandler.startDocument();
            final AttributesImpl atts = new AttributesImpl();
            atts.addAttribute(nsu, Resources.EMPTY_STRING, "name", "ID",
                    bufferedReader.readLine());
            atts.addAttribute(nsu, Resources.EMPTY_STRING, "version", "CDATA",
                    bufferedReader.readLine());
            contentHandler.startElement(nsu, rootElement, rootElement, atts);
            newLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                writeOption(line);
                newLine();
            }
            contentHandler.endElement(nsu, rootElement, rootElement);
            newLine();
            contentHandler.endDocument();
        }

        private void writeElement(String uri, String localName, String qName,
                                  Attributes atts)
                throws SAXException {
            contentHandler.startElement(uri, localName, qName, atts);
            contentHandler.endElement(uri, localName, qName);
        }

        private void newLine()
                throws SAXException {
            contentHandler.ignorableWhitespace(indent.toCharArray(),
                    0, // start index
                    indent.length());
        }

        /**
         * @param entry
         * @throws SAXException
         */
        private void writeOption(String entry)
                throws SAXException {
            final StringTokenizer stringTokenizer =
                    new StringTokenizer(entry, Resources.SYMBOL_ASTERISK);
            while (stringTokenizer.hasMoreElements()) {
                final AttributesImpl atts = new AttributesImpl();
                final String name = stringTokenizer.nextToken();
                final String value = stringTokenizer.nextToken();
                atts.addAttribute(nsu, Resources.EMPTY_STRING, "name", "ID",
                        name);
                atts.addAttribute(nsu, Resources.EMPTY_STRING, "value", "CDATA",
                        value);
                writeElement(nsu, "option", "option", atts);
            }
        }

        public ContentHandler getContentHandler() {
            return contentHandler;  //To change body of implemented methods use Options | File Templates.
        }

        public void setContentHandler(ContentHandler handler) {
            contentHandler = handler;
            //To change body of implemented methods use Options | File Templates.
        }

        public void parse(String systemId)
                throws IOException, SAXException {
            //To change body of implemented methods use Options | File Templates.
        }

        public boolean getFeature(String name)
                throws SAXNotRecognizedException, SAXNotSupportedException {
            return false;  //To change body of implemented methods use Options | File Templates.
        }

        public void setFeature(String name, boolean value)
                throws SAXNotRecognizedException, SAXNotSupportedException {
            //To change body of implemented methods use Options | File Templates.
        }

        public DTDHandler getDTDHandler() {
            return null;  //To change body of implemented methods use Options | File Templates.
        }

        public void setDTDHandler(DTDHandler handler) {
            //To change body of implemented methods use Options | File Templates.
        }

        public EntityResolver getEntityResolver() {
            return null;  //To change body of implemented methods use Options | File Templates.
        }

        public void setEntityResolver(EntityResolver resolver) {
            //To change body of implemented methods use Options | File Templates.
        }

        public ErrorHandler getErrorHandler() {
            return null;  //To change body of implemented methods use Options | File Templates.
        }

        public void setErrorHandler(ErrorHandler handler) {
            //To change body of implemented methods use Options | File Templates.
        }

        public Object getProperty(String name)
                throws SAXNotRecognizedException, SAXNotSupportedException {
            return null;  //To change body of implemented methods use Options | File Templates.
        }

        public void setProperty(String name, Object value)
                throws SAXNotRecognizedException, SAXNotSupportedException {
            //To change body of implemented methods use Options | File Templates.
        }


    }
}

