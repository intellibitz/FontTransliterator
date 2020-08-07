package sted

import sted.util.FileHelper
import sted.util.Resources
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.util.logging.LogManager
import java.util.logging.Logger

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val logger = Logger.getLogger("sted.Main")
        logmanager?.addLogger(logger)
        if (args.isNotEmpty()) {
            val param1 = args[0]
            // launch Console
            if (param1.toLowerCase().startsWith("-c")) {
                logger.info("Launching STED Console: ")
                val len = args.size
                val args1 = arrayOfNulls<String>(len - 1)
                System.arraycopy(args, 1, args1, 0, len - 1)
                STEDConsole.main(args1)
            }
        } else {
            logger.info("Launching STED GUI: ")
            // launch GUI
            STEDGUI.main(args)
        }
    }

    private var logManager: LogManager? = null//To change body of catch statement use Options | File Templates.

    //To change body of catch statement use Options | File Templates.
    @JvmStatic
    val logmanager: LogManager?
        get() {
            if (logManager == null) {
                logManager = LogManager.getLogManager()
                try {
                    with(logManager) {
                        this?.readConfiguration(
                            BufferedInputStream(
                                FileHelper.getInputStream(
                                    File(
                                        FileHelper.suffixFileSeparator(
                                            System.getProperty(
                                                Resources.LOG_PATH,
                                                "log"
                                            )
                                        ) +
                                                Resources
                                                    .getResource(
                                                        Resources.LOG_CONFIG_NAME
                                                    )
                                    )
                                )
                            )
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace() //To change body of catch statement use Options | File Templates.
                } catch (e: SecurityException) {
                    e.printStackTrace() //To change body of catch statement use Options | File Templates.
                }
            }
            return logManager
        }

}

class App {
    val greeting: String
        get() {
            return "Hello world."
        }
}

fun main(args: Array<String>) {
    println(App().greeting + args)
}
