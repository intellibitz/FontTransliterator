package sted

import sted.Main.addLogger
import sted.ui.AboutText
import sted.ui.STEDWindow
import sted.widgets.SplashWindow
import java.awt.Component
import java.awt.Cursor
import java.awt.Point
import java.awt.Toolkit
import java.io.File
import java.util.logging.Logger
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

class STEDGUI {
    companion object {
        private val logger = Logger.getLogger(STEDGUI::class.java.name)
        var sTEDWindow: STEDWindow? = null
            private set

        fun getSTEDWindow(component: Component?): STEDWindow? {
            // if STEDWindow is already found.. return from cache
            if (null != sTEDWindow) {
                return sTEDWindow
            }
            var parent: Component
            var src = component
            do {
                parent = src!!.parent
                if (parent is STEDWindow) {
                    sTEDWindow = parent
                    return sTEDWindow
                }
                src = parent
            } while (true)
        }

        @JvmStatic
        fun busy() {
            sTEDWindow!!.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
        }

        @JvmStatic
        fun relax() {
            sTEDWindow!!.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        }

        @JvmStatic
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

    }

    init {
        val splashWindow = SplashWindow(AboutText.instance)
        centerComponent(splashWindow)
        splashWindow.isVisible = true
        addLogger(logger)
        splashWindow.setProgress(10)
        sTEDWindow = STEDWindow()
        sTEDWindow!!.addStatusListener(splashWindow)
        sTEDWindow!!.init()
        sTEDWindow!!.load()
        splashWindow.setProgress(90)
        sTEDWindow!!.isVisible = true
        val fileName = System.getProperty("fontmap.file")
        if (fileName.isNullOrBlank()) {
//            File file = new File(Resources.getSampleFontMap());
//            stedWindow.getDesktop().openFontMap(file);
            sTEDWindow!!.desktop.newFontMap()
        } else {
            sTEDWindow!!.desktop
                .openFontMap(File(fileName))
        }
        splashWindow.setProgress(100)
        splashWindow.dispose()
    }
}