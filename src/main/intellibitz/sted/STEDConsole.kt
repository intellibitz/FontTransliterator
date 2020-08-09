package sted

import sted.Main.addLogger
import sted.event.IThreadListener
import sted.event.ThreadEvent
import sted.fontmap.Converter
import sted.fontmap.FontMap
import sted.io.FontMapReader
import sted.io.Resources
import java.io.File
import java.util.logging.Logger
import kotlin.system.exitProcess

class STEDConsole : IThreadListener {
    lateinit var fontMapName: String
    lateinit var inputFileName: String
    lateinit var outputFileName: String
    private var reverse = false
    private var html = false

    fun run() {
        try {
            val input = File(inputFileName)
            val output = File(outputFileName)
            // create console based FontMap.. no need to readFontMap fonts
            val fontMap = FontMap(File(fontMapName), true)
            FontMapReader.read(fontMap)
            val converter = Converter(fontMap, input, output)
            converter.setReverseTransliterate(reverse)
            converter.setHTMLAware(html)
            converter.addThreadListener(this)
            logger.info("Running Transliterator with the following options: ")
            logger.info("   FontMap: $fontMapName")
            logger.info("   Input: $inputFileName")
            logger.info("   Output: $outputFileName")
            logger.info("   Preserve Tags in Transliteration: $html")
            logger.info("   Reverse Transliteration: $reverse")
            converter.start()
        } catch (e: Exception) {
            e.printStackTrace() //To change body of catch statement use Options | File Templates.
            logger.severe("Exception : " + e.message)
            logger.throwing("sted.STEDConsole", "Constructor", e)
            exitProcess(-1)
        }
    }

    override fun threadRunStarted(threadEvent: ThreadEvent) {}
    override fun threadRunning(threadEvent: ThreadEvent) {}
    override fun threadRunFailed(threadEvent: ThreadEvent) {
        logger.severe(threadEvent.eventSource.message.toString())
        exitProcess(-1)
    }

    override fun threadRunFinished(threadEvent: ThreadEvent) {
        logger.info(threadEvent.eventSource.message.toString())
        exitProcess(0)
    }

    private fun loadArgs(args: Array<String>) {
        console.fontMapName = System.getProperty(Resources.FONTMAP_FILE)
        console.inputFileName = System.getProperty(Resources.INPUT_FILE)
        console.outputFileName = System.getProperty(Resources.OUTPUT_FILE)
        for (param in args) {
            when {
                param.startsWith("-map=") -> {
                    console.fontMapName = param.substring(5)
                }
                param.startsWith("-in=") -> {
                    console.inputFileName = param.substring(4)
                }
                param.startsWith("-out=") -> {
                    console.outputFileName = param.substring(5)
                }
            }
        }
        console.reverse = args.contains("-r") || args.contains("-R")
        console.html = args.contains("-p") || args.contains("-P")
        if (console.fontMapName.isBlank()) {
            logger.info("Invalid FontMap: $console.fontMapName")
            printUsage()
            exitProcess(1)
        }
        if (console.inputFileName.isBlank()) {
            logger.info("Invalid Input File: $console.inputFileName")
            printUsage()
            exitProcess(2)
        }
        if (console.outputFileName.isBlank()) {
            logger.info("Invalid Output File: $console.outputFileName")
            printUsage()
            exitProcess(3)
        }
    }

    fun run(args: Array<String>) {
        loadArgs(args)
        run()
    }

    companion object {
        val logger: Logger = Logger.getLogger(STEDConsole::class.java.name)
        val console = STEDConsole()

        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Launching STED Console: ")
            console.run(args)
        }

        private fun printUsage() {
            logger.info("STED Console Usage: ")
            logger.info(
                "   java -Dfontmap.file='<file>' -Dinput.file='<input>' -Doutput.file='<output>' sted.STEDConsole"
            )
            logger.info(" -OR- ")
            logger.info(
                "   java sted.STEDConsole -map='<file>' -in='<input>' -out='<output>'"
            )
        }
    }

    init {
        addLogger(logger)
    }
}