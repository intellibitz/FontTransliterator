package intellibitz.sted.io;

import intellibitz.sted.fontmap.FontMap;
import intellibitz.sted.util.Resources;
import org.xml.sax.InputSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

public class FontMapXMLWriter {
    private FontMapXMLWriter() {
    }

    public static void write(FontMap fontMap)
            throws TransformerException {
        final File selectedFile = fontMap.getFontMapFile();
// Use a Transformer for output
        final Transformer transformer =
                TransformerFactory.newInstance().newTransformer();
// Use the parser as a SAX source for input
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(selectedFile.getName());
        stringBuilder.append(Resources.NEWLINE_DELIMITER);
        stringBuilder.append(Resources.getVersion());
        stringBuilder.append(Resources.NEWLINE_DELIMITER);
        stringBuilder.append(fontMap.toString());
        final SAXSource source = new SAXSource(new FontMapXMLReader(),
                new InputSource(
                        new BufferedReader(
                                new StringReader(
                                        stringBuilder.toString()))));
        transformer.transform(source, new StreamResult(selectedFile));

    }

}
