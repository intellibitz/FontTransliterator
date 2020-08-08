package sted

import java.util.logging.LogManager
import java.util.logging.Logger

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val logger = Logger.getLogger("sted.Main")
        addLogger(logger)
        if (args.isNotEmpty()) {
            val param1 = args[0]
            // launch Console
            if (param1.toLowerCase().startsWith("-c")) {
                logger.info("Launching STED Console: ")
                STEDConsole(args)
            }
        } else {
            logger.info("Launching STED GUI: ")
            // launch GUI
            STEDGUI()
        }
    }

    @JvmStatic
    fun addLogger(logger: Logger) {
        logmanager?.addLogger(logger)
    }

    private var logManager: LogManager? = null

    @JvmStatic
    val logmanager: LogManager?
        get() {
            if (logManager == null) {
                logManager = LogManager.getLogManager()
                try {
                    with(logManager) {
                        this?.readConfiguration(
                            ClassLoader.getSystemResourceAsStream("log/logging.properties")
//                            FileHelper.getInputStream(File("log/logging.properties"))
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace() //To change body of catch statement use Options | File Templates.
                }
            }
            return logManager
        }

}

class App {
    val projectName: String
        get() {
            return "FontTransliterator - STED."
        }
}

fun main(args: Array<String>) {
    println(App().projectName + args)
    Main.main(args)
}
