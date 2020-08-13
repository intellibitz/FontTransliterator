package sted.io

import org.xml.sax.*
import org.xml.sax.helpers.AttributesImpl
import org.xml.sax.helpers.DefaultHandler
import java.io.BufferedReader
import java.io.IOException
import java.util.*

/**
 * FontMapXMLReader reads the FontMap into an xml document format generates sax
 * events that can be used to transform fontmap to xml document
 */
internal class FontMapXMLReader : XMLReader {
    // We're not doing namespaces, and we have no
    // attributes on our elements.
    private val nsu = "" // NamespaceURI
    private var contentHandler: ContentHandler? = null

    @Throws(IOException::class, SAXException::class)
    override fun parse(input: InputSource) {
        if (contentHandler == null) {
            throw SAXException("No content contentHandler")
        }
        val rootElement = "fontmap"
        // Get an efficient reader for the file
        val bufferedReader = BufferedReader(input.characterStream)
        // Read the file and display it's contents.
        contentHandler!!.startDocument()
        val atts = AttributesImpl()
        atts.addAttribute(
            nsu, "", "name", "ID",
            bufferedReader.readLine()
        )
        atts.addAttribute(
            nsu, "", "version", "CDATA",
            bufferedReader.readLine()
        )
        contentHandler!!.startElement(nsu, rootElement, rootElement, atts)
        newLine()
        contentHandler!!.startElement(nsu, "font", "font", EMPTY_ATTRIBUTES)
        newLine()
        writeElement(
            "font_from", "font_from",
            getFontAttributes(bufferedReader.readLine())
        )
        newLine()
        writeElement(
            "font_to", "font_to",
            getFontAttributes(bufferedReader.readLine())
        )
        newLine()
        contentHandler!!.endElement(nsu, "font", "font")
        newLine()
        var line: String
        while (bufferedReader.readLine().also { line = it } != null) {
            writeFontEntry(line)
            newLine()
        }
        contentHandler!!.endElement(nsu, rootElement, rootElement)
        newLine()
        contentHandler!!.endDocument()
    }

    private fun getFontAttributes(line: String): Attributes {
        val atts = AttributesImpl()
        val stringTokenizer = StringTokenizer(line, "*")
        atts.addAttribute(
            nsu, "", "value", "CDATA",
            stringTokenizer.nextToken()
        )
        atts.addAttribute(
            nsu, "", "path", "CDATA",
            stringTokenizer.nextToken()
        )
        return atts
    }

    private fun getValueAttribute(`val`: String): Attributes {
        val atts = AttributesImpl()
        atts.addAttribute("", "", "value", "CDATA", `val`)
        return atts
    }

    @Throws(SAXException::class)
    private fun writeElement(localName: String, qName: String, atts: Attributes) {
        contentHandler!!.startElement("", localName, qName, atts)
        contentHandler!!.endElement("", localName, qName)
    }

    @Throws(SAXException::class)
    private fun newLine() {
        // for readability
        contentHandler!!.ignorableWhitespace(
            "\n".toCharArray(),
            0,  // start index
            "\n".length
        )
    }

    /**
     * <font_entry> <entry_from value="NA"></entry_from> <entry_to value="��"></entry_to>
     * <begins_with></begins_with> <ends_with></ends_with> <followed_by value="Nu"></followed_by> <preceded_by value="Nu"></preceded_by> <conditional value="AND"></conditional> </font_entry>
     *
     */
    @Throws(SAXException::class)
    private fun writeFontEntry(entry: String) {
        contentHandler!!.startElement(
            nsu, "font_entry", "font_entry",
            EMPTY_ATTRIBUTES
        )
        newLine()
        val stringTokenizer = StringTokenizer(entry, ":")
        var i = 0
        while (stringTokenizer.hasMoreElements()) {
            val token = stringTokenizer.nextToken()
            if (!(token.toLowerCase().startsWith("null")
                        || token.toLowerCase().startsWith("false")
                        || "" == token)
            ) {
                when (i) {
                    0 -> {
                        writeElement(
                            "entry_from", "entry_from",
                            getValueAttribute(token)
                        )
                        newLine()
                    }
                    1 -> {
                        writeElement(
                            "entry_to", "entry_to",
                            getValueAttribute(token)
                        )
                        newLine()
                    }
                    2 -> {
                        writeElement(
                            "begins_with", "begins_with",
                            getValueAttribute(token)
                        )
                        newLine()
                    }
                    3 -> {
                        writeElement(
                            "ends_with", "ends_with",
                            getValueAttribute(token)
                        )
                        newLine()
                    }
                    4 -> {
                        writeElement(
                            "followed_by", "followed_by",
                            getValueAttribute(token)
                        )
                        newLine()
                    }
                    5 -> {
                        writeElement(
                            "preceded_by", "preceded_by",
                            getValueAttribute(token)
                        )
                        newLine()
                    }
                    6 -> {
                        writeElement(
                            "conditional", "conditional",
                            getValueAttribute(token)
                        )
                        newLine()
                    }
                    else -> {
                    }
                }
            }
            i++
        }
        contentHandler!!.endElement(nsu, "font_entry", "font_entry")
    }

    override fun getContentHandler(): ContentHandler {
        return contentHandler!! //To change body of implemented methods use Options | File Templates.
    }

    override fun setContentHandler(handler: ContentHandler) {
        contentHandler = handler
        //To change body of implemented methods use Options | File Templates.
    }

    @Throws(IOException::class, SAXException::class)
    override fun parse(systemId: String) {
        //To change body of implemented methods use Options | File Templates.
    }

    @Throws(SAXNotRecognizedException::class, SAXNotSupportedException::class)
    override fun getFeature(name: String): Boolean {
        return false //To change body of implemented methods use Options | File Templates.
    }

    @Throws(SAXNotRecognizedException::class, SAXNotSupportedException::class)
    override fun setFeature(name: String, value: Boolean) {
        //To change body of implemented methods use Options | File Templates.
    }

    override fun getDTDHandler(): DTDHandler {
        return DefaultHandler() //To change body of implemented methods use Options | File Templates.
    }

    override fun setDTDHandler(handler: DTDHandler) {
        //To change body of implemented methods use Options | File Templates.
    }

    override fun getEntityResolver(): EntityResolver {
        return DefaultHandler() //To change body of implemented methods use Options | File Templates.
    }

    override fun setEntityResolver(resolver: EntityResolver) {
        //To change body of implemented methods use Options | File Templates.
    }

    override fun getErrorHandler(): ErrorHandler {
        return DefaultHandler() //To change body of implemented methods use Options | File Templates.
    }

    override fun setErrorHandler(handler: ErrorHandler) {
        //To change body of implemented methods use Options | File Templates.
    }

    @Throws(SAXNotRecognizedException::class, SAXNotSupportedException::class)
    override fun getProperty(name: String): Any {
        return Any() //To change body of implemented methods use Options | File Templates.
    }

    @Throws(SAXNotRecognizedException::class, SAXNotSupportedException::class)
    override fun setProperty(name: String, value: Any) {
        //To change body of implemented methods use Options | File Templates.
    }

    companion object {
        private val EMPTY_ATTRIBUTES: Attributes = AttributesImpl()
    }
}