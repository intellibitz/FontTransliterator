package sted.ui

import sted.io.Resources
import sted.widgets.AboutDialog
import java.awt.Component

/**
 * Displays the About message.
 */
class AboutSTED(title: String?, aboutDescriptor: Component?) : AboutDialog(title, aboutDescriptor) {
    companion object {
        private var aboutSTED: AboutSTED? = null
        @JvmStatic
        @get:Synchronized
        val instance: AboutSTED?
            get() {
                if (aboutSTED == null) {
                    aboutSTED = AboutSTED(
                        Resources.getResource(Resources.TITLE_ABOUT_STED),
                        AboutText.instance
                    )
                }
                return aboutSTED
            }
    }
}