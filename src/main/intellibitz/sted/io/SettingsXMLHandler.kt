package sted.io

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import sted.io.FileHelper.getInputStream
import java.io.File
import java.io.IOException
import java.util.*
import java.util.logging.Logger
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

/**
 * Reads the Settings XML file
 */
class SettingsXMLHandler : DefaultHandler() {
    val settings: MutableMap<String, String> = HashMap()
    private lateinit var stedSettingsFile: File
    private var key: String? = ""
    private var value = ""
    fun init(stedSettings: File) {
        stedSettingsFile = stedSettings
    }

    @Throws(IOException::class, ParserConfigurationException::class, SAXException::class)
    fun read() {
        logger.entering(javaClass.name, "read", stedSettingsFile)
        val saxParserFactory = SAXParserFactory.newInstance()
        saxParserFactory.isValidating = true
        val saxParser = saxParserFactory.newSAXParser()
        logger.info(
            "reading settings file - " +
                    stedSettingsFile.absolutePath
        )
        val inputStream = getInputStream(stedSettingsFile)
        saxParser.parse(inputStream, this)
        logger.exiting(javaClass.name, "read")
    }

    override fun startElement(
        uri: String, localName: String, qName: String,
        attributes: Attributes
    ) {
        if ("option" == qName) {
            key = attributes.getValue("name")
            value = attributes.getValue("value")
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        if ("option" == qName) {
            if (key != null) {
                settings[key!!] = value
            }
        }
    }

    companion object {
        private val logger = Logger.getLogger("sted.io.SettingsXMLHandler")
    }
}