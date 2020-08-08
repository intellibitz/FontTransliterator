package sted.fontmap

import sted.event.ThreadEventSourceBase
import sted.event.TransliterateEvent
import sted.io.FileHelper.fileCopy
import java.io.*
import java.util.logging.Logger

open class Converter() : ThreadEventSourceBase() {
    private var stopRequested = false
    private var fontMap: FontMap? = null
    private var fileToConvert: File? = null
    private var convertedFile: File? = null
    private var initialized = false
    private var success = false
    var transliterate: ITransliterate? = null
        private set

    constructor(fontMap: FontMap, input: File, output: File) : this() {
        init(fontMap, input, output)
    }

    private fun init(fontMap: FontMap, input: File, output: File) {
        fileToConvert = input
        convertedFile = output
        initialized = true
        success = false
        stopRequested = false
        setFontMap(fontMap)
    }

    private fun initTransliterator() {
        if (transliterate == null) {
            transliterate = DefaultTransliterator()
        }
    }

    override fun run() {
        fireThreadRunStarted()
        if (!initialized) {
            throw IllegalThreadStateException(
                "Thread should be initialized.. Call init method before invoking run"
            )
        }
        convertFile()
        initialized = false
        if (success) {
            fireThreadRunFinished()
        } else {
            fireThreadRunFailed()
        }
    }

    fun setSuccess(success: Boolean) {
        this.success = success
    }

    val isReady: Boolean
        get() = fontMap != null && !fontMap!!.entries.isEmpty

    fun setHTMLAware(flag: Boolean) {
        transliterate!!.setHTMLAware(flag)
    }

    private fun convertFile() {
        try {
            fileCopy(
                fileToConvert,
                fileToConvert!!.absolutePath + ".bakup"
            )
        } catch (e: IOException) {
            message = "Unable to Backup input file: " + e.message
            success = false
            logger.severe("Unable to Backup input file: " + e.message)
            logger.throwing(javaClass.name, "convertFile", e)
            return
        }
        val bufferedReader: BufferedReader
        try {
            bufferedReader = BufferedReader(FileReader(fileToConvert!!))
        } catch (e: FileNotFoundException) {
            message = "File Not Found: " + e.message
            success = false
            logger.severe("Cannot Read - File Not Found: " + e.message)
            logger.throwing(javaClass.name, "convertFile", e)
            return
        }
        val bufferedWriter: BufferedWriter
        try {
            bufferedWriter = BufferedWriter(FileWriter(convertedFile!!))
        } catch (e: IOException) {
            message = "Cannot create Writer: " + e.message
            success = false
            logger.severe("Cannot Write - IOException: " + e.message)
            logger.throwing(javaClass.name, "convertFile", e)
            return
        }
        var input: String?
        try {
            while (bufferedReader.readLine().also { input = it } != null) {
                if (stopRequested) {
                    break
                }
                bufferedWriter.write(transliterate!!.parseLine(input)!!)
                bufferedWriter.newLine()
                fireThreadRunning()
            }
        } catch (e: IOException) {
            message = "IOException: " + e.message
            success = false
            logger.severe(
                "IOException - Ceasing Conversion: " + e.message
            )
            logger.throwing(javaClass.name, "convertFile", e)
            return
        } finally {
            try {
                bufferedReader.close()
                bufferedWriter.close()
            } catch (e: IOException) {
                message = "Cannot Close Reader/Writer - IOException: " +
                        e.message
                success = false
                logger.severe(
                    "Cannot close File Streams - Ceasing Conversion: " +
                            e.message
                )
                logger.throwing(javaClass.name, "convertFile", e)
            }
        }
        if (!stopRequested) {
            message = "Transliterate Done."
            success = true
        }
    }

    fun setReverseTransliterate(flag: Boolean) {
        transliterate!!.setReverseTransliterate(flag)
    }

    fun setFontMap(fontMap: FontMap?) {
        this.fontMap = fontMap
        if (fontMap != null) {
            transliterate!!.setEntries(fontMap.entries)
        }
    }

    @Synchronized
    fun setStopRequested(flag: Boolean) {
        stopRequested = flag
    }

    companion object {
        private val logger = Logger.getLogger("sted.fontmap.Converter")
    }

    init {
        threadEvent = TransliterateEvent(this)
        initTransliterator()
    }
}