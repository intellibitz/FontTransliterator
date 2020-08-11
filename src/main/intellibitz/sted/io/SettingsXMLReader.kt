package sted.io

import org.xml.sax.*
import org.xml.sax.helpers.AttributesImpl
import org.xml.sax.helpers.DefaultHandler
import java.io.BufferedReader
import java.io.IOException
import java.util.*

/**
 * FontMapXMLReader reads the FontMap into an xml document format generates
 * sax events that can be used to transform fontmap to xml document
 */
internal class SettingsXMLReader : XMLReader {
    // We're not doing namespaces, and we have no    // attributes on our elements.
    private val nsu = "" // NamespaceURI
    private var contentHandler: ContentHandler? = null

    @Throws(IOException::class, SAXException::class)
    override fun parse(input: InputSource) {
        if (contentHandler == null) {
            throw SAXException("No content contentHandler")
        }
        val rootElement = "settings"
        // Get an efficient reader for the file
        val bufferedReader = BufferedReader(input.characterStream)
        // Read the file and display it's contents.
        contentHandler!!.startDocument()
        val atts = AttributesImpl()
        atts.addAttribute(
            nsu, Resources.EMPTY_STRING, "name", "ID",
            bufferedReader.readLine()
        )
        atts.addAttribute(
            nsu, Resources.EMPTY_STRING, "version", "CDATA",
            bufferedReader.readLine()
        )
        contentHandler!!.startElement(nsu, rootElement, rootElement, atts)
        newLine()
        var line: String
        while (bufferedReader.readLine().also { line = it } != null) {
            writeOption(line)
            newLine()
        }
        contentHandler!!.endElement(nsu, rootElement, rootElement)
        newLine()
        contentHandler!!.endDocument()
    }

    @Throws(SAXException::class)
    private fun writeElement(atts: Attributes) {
        contentHandler!!.startElement("", "option", "option", atts)
        contentHandler!!.endElement("", "option", "option")
    }

    @Throws(SAXException::class)
    private fun newLine() {
        // for readability
        val indent = "\n"
        contentHandler!!.ignorableWhitespace(
            indent.toCharArray(),
            0,  // start index
            indent.length
        )
    }

    @Throws(SAXException::class)
    private fun writeOption(entry: String) {
        val stringTokenizer = StringTokenizer(entry, Resources.SYMBOL_ASTERISK)
        while (stringTokenizer.hasMoreElements()) {
            val atts = AttributesImpl()
            val name = stringTokenizer.nextToken()
            val value = stringTokenizer.nextToken()
            atts.addAttribute(
                nsu, Resources.EMPTY_STRING, "name", "ID",
                name
            )
            atts.addAttribute(
                nsu, Resources.EMPTY_STRING, "value", "CDATA",
                value
            )
            writeElement(atts)
        }
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
}