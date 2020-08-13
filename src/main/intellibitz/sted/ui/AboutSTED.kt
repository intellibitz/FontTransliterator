package sted.ui

import sted.io.Resources
import sted.widgets.AboutDialog
import java.awt.Component

class AboutSTED(title: String?, aboutDescriptor: Component?) : AboutDialog(title, aboutDescriptor) {
    companion object {
        val instance: AboutSTED by lazy {
            AboutSTED(
                Resources.getResource("title.about.sted"),
                AboutText.instance
            )
        }
    }
}