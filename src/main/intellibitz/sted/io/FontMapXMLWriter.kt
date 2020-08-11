package sted.io

import org.xml.sax.InputSource
import sted.fontmap.FontMap
import sted.io.Resources.version
import java.io.BufferedReader
import java.io.StringReader
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult

object FontMapXMLWriter {
    @Throws(TransformerException::class)
    fun write(fontMap: FontMap) {
        val selectedFile = fontMap.fontMapFile
        // Use a Transformer for output
        val transformer = TransformerFactory.newInstance().newTransformer()
        // Use the parser as a SAX source for input
        val stringBuilder = """
            ${selectedFile.name}
            $version
            $fontMap
            """.trimIndent()
        val source = SAXSource(
            FontMapXMLReader(),
            InputSource(
                BufferedReader(
                    StringReader(
                        stringBuilder
                    )
                )
            )
        )
        transformer.transform(source, StreamResult(selectedFile))
    }
}