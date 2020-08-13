package sted.io

import org.xml.sax.SAXException
import sted.event.FontMapReadEvent
import sted.event.ThreadEventSourceBase
import sted.fontmap.FontMap
import sted.fontmap.FontMapEntry
import sted.io.FileHelper.getInputStream
import java.awt.HeadlessException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Logger
import javax.xml.parsers.ParserConfigurationException

class FontMapReader  //
    : ThreadEventSourceBase() {
    private var fontMap = FontMap()
    fun init(fontMap: FontMap) {
        val file = fontMap.fontMapFile
        require(!file.path.isNullOrEmpty()) { "Cannot Load - File path is empty" }
        this.fontMap = fontMap
        // create a FontMapReadEvent.. Identifies the Event with FontMapReader
        threadEvent = FontMapReadEvent(this)
    }

    /**
     * If this thread was constructed using a separate `Runnable` run
     * object, then that `Runnable` object's `run` method
     * is called; otherwise, this method does nothing and returns.
     *
     *
     * Subclasses of `Thread` should override this method.
     *
     * @see Thread.start
     * @see Thread.stop
     * @see Runnable.run
     */
    override fun run() {
        fireThreadRunStarted()
        try {
            read(fontMap)
            fireThreadRunFinished()
        } catch (e: IOException) {
            message = (
                    "Invalid FontMap " +
                            fontMap.fontMapFile.absolutePath
                    )
            logger.throwing(
                "sted.actions.LoadFontMapAction",
                "readFontMap", e
            )
            fireThreadRunFailed()
        } catch (e: SAXException) {
            message = (
                    "Invalid FontMap " +
                            fontMap.fontMapFile.absolutePath
                    )
            logger.throwing(
                "sted.actions.LoadFontMapAction",
                "readFontMap", e
            )
            fireThreadRunFailed()
        } catch (e: ParserConfigurationException) {
            message = (
                    "Invalid FontMap " +
                            fontMap.fontMapFile.absolutePath
                    )
            logger.throwing(
                "sted.actions.LoadFontMapAction",
                "readFontMap", e
            )
            fireThreadRunFailed()
        } catch (e: HeadlessException) {
            message = (
                    "Invalid FontMap " +
                            fontMap.fontMapFile.absolutePath
                    )
            logger.throwing(
                "sted.actions.LoadFontMapAction",
                "readFontMap", e
            )
            fireThreadRunFailed()
        }
    }

    companion object {
        private const val FONT1_TITLE = "FONT1"
        private const val FONT2_TITLE = "FONT2"
        private const val HEADER = "FONT MAPPER 1.0"
        private const val HEADER_TITLE = "HEADER"
        private const val INT_DELIMITER = ":"
        private const val PROPERTY_DELIMITER = "="
        private const val STARTS_WITH = "STARTS"
        private const val ENDS_WITH = "ENDS"
        private const val FOLLOWED_BY = "FBY"
        private const val PRECEDED_BY = "PBY"
        private const val COMMA = ","
        private val logger = Logger.getLogger("sted.io.FontMapReader")

        @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
        fun read(fontMap: FontMap) {
            val file = fontMap.fontMapFile
            if (file.name.toLowerCase().endsWith("xml")) {
                FontMapXMLHandler().read(fontMap)
            } else {
                readOldFormat(fontMap)
            }
        }

        @Throws(IOException::class)
        private fun readOldFormat(fontMap: FontMap) {
            val file = fontMap.fontMapFile
            val bufferedReader = BufferedReader(InputStreamReader(getInputStream(file)))
            var input: String
            var headerFound = false
            while (bufferedReader.readLine().also { input = it } != null) {
                if (input.isNotEmpty() && !(input.startsWith("#") ||
                            input.startsWith("//") ||
                            input.startsWith("/*"))
                ) {
                    if (input.indexOf(COMMA) != -1) {
                        val entry = createFontMapEntry(input)
                        if (entry != null)
                            fontMap.entries.add(entry)
                        continue
                    }
                    val stringTokenizer = StringTokenizer(
                        input.trim { it <= ' ' }, PROPERTY_DELIMITER, false
                    )
                    if (stringTokenizer.countTokens() < 2) {
                        throw IOException("$file invalid fontmap - no header found")
                    }
                    val token1 = stringTokenizer.nextToken()
                    val token2 = stringTokenizer.nextToken()
                    // first line must always be a header
                    if (!headerFound && HEADER_TITLE == token1) {
                        if (HEADER == token2) {
                            headerFound = true
                            continue
                        }
                    }
                    // if header is not found yet, then first line is not an header -- quit!
                    if (!headerFound) {
                        throw IOException(
                            file.toString() +
                                    " invalid fontmap - no header found - quitting"
                        )
                    }
                    when (token1) {
                        FONT1_TITLE -> {
                            fontMap.setFont1(token2)
                        }
                        FONT2_TITLE -> {
                            fontMap.setFont2(token2)
                        }
                        else -> {
                            val fontMapEntry = FontMapEntry()
                            fontMapEntry.init(token1, token2)
                            fontMap.entries.add(fontMapEntry)
                        }
                    }
                }
            }
        }

        private fun createFontMapEntry(value: String): FontMapEntry? {
            var entry: FontMapEntry? = null
            val stringTokenizer = StringTokenizer(value, INT_DELIMITER)
            if (stringTokenizer.countTokens() == 2) {
                entry = FontMapEntry()
                val key = stringTokenizer.nextToken()
                var st = StringTokenizer(key, PROPERTY_DELIMITER)
                if (st.countTokens() == 2) {
                    entry.from = st.nextToken()
                    entry.to = st.nextToken()
                }
                st = StringTokenizer(stringTokenizer.nextToken(), COMMA)
                while (st.hasMoreTokens()) {
                    val st2 = StringTokenizer(st.nextToken(), PROPERTY_DELIMITER)
                    if (st2.countTokens() == 2) {
                        when (st2.nextToken()) {
                            STARTS_WITH -> {
                                entry.isBeginsWith = st2.nextToken().toBoolean()
                            }
                            ENDS_WITH -> {
                                entry.isEndsWith = st2.nextToken().toBoolean()
                            }
                            FOLLOWED_BY -> {
                                entry.followedBy = st2.nextToken()
                            }
                            PRECEDED_BY -> {
                                entry.precededBy = st2.nextToken()
                            }
                        }
                    }
                }
            }
            return entry
        }
    }
}