package sted

import sted.io.Resources
import sted.ui.AboutText
import sted.ui.MenuHandler
import sted.ui.STEDWindow
import sted.widgets.SplashWindow
import java.awt.Component
import java.awt.Cursor
import java.awt.Point
import java.awt.Toolkit
import java.io.File
import java.util.logging.LogManager
import java.util.logging.Logger
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

object STEDGUI {
    val logger: Logger = Logger.getLogger(STEDGUI::class.java.name)
    val sTEDWindow: STEDWindow = STEDWindow()
    private val logManager: LogManager = LogManager.getLogManager()

    fun run() {
        Resources.init()
        val resource = Resources.getResource("config.log")
        if (!resource.isNullOrEmpty()) {
            logManager.readConfiguration(ClassLoader.getSystemResourceAsStream(resource))
        }
        logManager.addLogger(logger)
        logger.info("Launching STED GUI: ")
        val splashWindow = SplashWindow(AboutText.instance)
        centerComponent(splashWindow)
        splashWindow.isVisible = true
        splashWindow.setProgress(10)

        val xml = Resources.getResource("config.menu")
        if (xml == null) {
            logger.severe("Load menu file not found: please check config.menu property to set menu file")
        } else {
            MenuHandler.loadMenu(xml)
        }

        sTEDWindow.addStatusListener(splashWindow)
        sTEDWindow.logManager = logManager
        sTEDWindow.init()
        sTEDWindow.load()
        splashWindow.setProgress(90)
        sTEDWindow.isVisible = true
        val fileName = Resources.getResource("fontmap.file")
        if (fileName.isNullOrBlank()) {
//            File file = new File(Resources.getSampleFontMap());
//            stedWindow.getDesktop().openFontMap(file);
            sTEDWindow.desktop.newFontMap()
        } else {
            sTEDWindow.desktop
                .openFontMap(File(fileName))
        }
        splashWindow.setProgress(100)
        splashWindow.dispose()
    }

    fun busy() {
        sTEDWindow.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
    }

    fun relax() {
        sTEDWindow.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
    }

    @Throws(
        ClassNotFoundException::class,
        InstantiationException::class,
        IllegalAccessException::class,
        UnsupportedLookAndFeelException::class
    )
    fun updateUIWithLAF(
        lookAndFeel: String?,
        iterator: Iterator<Component?>
    ) {
        UIManager.setLookAndFeel(lookAndFeel)
        while (iterator.hasNext()) {
            val component = iterator.next()
            SwingUtilities.updateComponentTreeUI(component)
        }
    }

    /**
     * A very nice trick is to center windows on screen
     *
     * @param component The `Component` to center
     */
    fun centerComponent(component: Component) {
        val dimension = Toolkit.getDefaultToolkit().screenSize
        val size = component.size
        component.location = Point(
            (dimension.width - size.width) / 2,
            (dimension.height - size.height) / 2
        )
    }

    @JvmStatic
    fun main(args: Array<String>) {
        run()
    }
}