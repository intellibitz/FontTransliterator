package sted.io

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import sted.fontmap.FontMap
import sted.fontmap.FontMapEntry
import sted.io.FileHelper.getInputStream
import java.io.IOException
import java.util.logging.Logger
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

internal class FontMapXMLHandler : DefaultHandler() {
    lateinit var fontMap: FontMap
    lateinit var fontMapEntry: FontMapEntry
    private var valid = false

    @Throws(IOException::class, ParserConfigurationException::class, SAXException::class)
    fun read(fontMap: FontMap) {
        logger.entering(javaClass.name, "read", fontMap)
        this.fontMap = fontMap
        val saxParserFactory = SAXParserFactory.newInstance()
        saxParserFactory.isValidating = true
        val saxParser = saxParserFactory.newSAXParser()
        logger.info("parsing file - " + fontMap.fontMapFile)
        val inputStream = getInputStream(fontMap.fontMapFile)
        saxParser.parse(inputStream, this)
        logger.exiting(javaClass.name, "read")
    }

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (qName) {
            "fontmap" -> {
                valid = true
            }
            "font_from" -> {
                fontMap.font1Path = attributes.getValue("path")
                fontMap.setFont1(attributes.getValue("value"))
            }
            "font_to" -> {
                fontMap.font2Path = attributes.getValue("path")
                fontMap.setFont2(attributes.getValue("value"))
            }
            "font_entry" -> {
                fontMapEntry = FontMapEntry()
            }
            "entry_from" -> {
                fontMapEntry.from = attributes.getValue("value")
            }
            "entry_to" -> {
                fontMapEntry.to = attributes.getValue("value")
            }
            "begins_with" -> {
                fontMapEntry.setBeginsWith(attributes.getValue("value"))
            }
            "ends_with" -> {
                fontMapEntry.setEndsWith(attributes.getValue("value"))
            }
            "preceded_by" -> {
                fontMapEntry.precededBy = attributes.getValue("value")
            }
            "followed_by" -> {
                fontMapEntry.followedBy = attributes.getValue("value")
            }
            "conditional" -> {
                fontMapEntry.setConditional(attributes.getValue("value"))
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (!valid) {
            throw SAXException(
                "Invalid FontMap.. Did not have a valid Header"
            )
        }
        if ("font_entry" == qName) {
            fontMap.entries.add(fontMapEntry)
        }
    }

    companion object {
        private val logger = Logger.getLogger("sted.io.FontMapXMLHandler")
    }
}