package sted

import sted.io.Resources
import java.util.logging.LogManager
import java.util.logging.Logger

object Main {
    @JvmStatic
    var logManager: LogManager = LogManager.getLogManager()

    @JvmStatic
    fun main(args: Array<String>) {
        val resource = Resources.getResource("config.log")
        if (!resource.isNullOrEmpty()) {
            logManager.readConfiguration(ClassLoader.getSystemResourceAsStream(resource))
        }
        val logger = Logger.getLogger(Main::class.java.name)
        addLogger(logger)
        logger.info("Begin STED: args - " + args.asList())
        if (args.isNullOrEmpty()) {
            // launch GUI
            STEDGUI.main(args)
        } else {
            // launch Console
            STEDConsole.main(args)
        }
    }

    @JvmStatic
    fun addLogger(logger: Logger) {
        logManager.addLogger(logger)
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
