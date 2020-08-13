package sted.io

import org.xml.sax.InputSource
import sted.io.Resources.getResource
import sted.io.Resources.version
import sted.ui.MenuHandler
import java.io.BufferedReader
import java.io.File
import java.io.StringReader
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult

object SettingsXMLWriter {
    @Throws(TransformerException::class)
    fun writeUserSettings() {
        val s = getResource("settings.sted.user")
        if (!s.isNullOrEmpty()) {
            val file = File(s)
            // Use the parser as a SAX source for input
            val stringBuffer = """
            ${file.name}
            $version
            ${MenuHandler.userOptions}
            """.trimIndent()
            write(file, stringBuffer)
        }
    }

    @Throws(TransformerException::class)
    private fun write(file: File, fileContents: String) {
        // Use a Transformer for output
        val transformer = TransformerFactory.newInstance().newTransformer()
        val source = SAXSource(
            SettingsXMLReader(),
            InputSource(
                BufferedReader(
                    StringReader(
                        fileContents
                    )
                )
            )
        )
        transformer.transform(source, StreamResult(file))
    }
}