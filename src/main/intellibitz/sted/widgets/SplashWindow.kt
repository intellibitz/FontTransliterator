package sted.widgets

import sted.event.IStatusListener
import sted.event.StatusEvent
import java.awt.Component
import javax.swing.BoxLayout
import javax.swing.JProgressBar
import javax.swing.JWindow

class SplashWindow(component: Component?) : JWindow(), IStatusListener {
    private val progress: JProgressBar
    fun setProgress(percent: Int) {
        progress.value = percent
    }

    override fun statusPosted(event: StatusEvent) {
        event.status?.toInt()?.let { setProgress(it) }
    }

    init {
        val contentPane = contentPane
        contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)
        contentPane.add(component)
        progress = JProgressBar(0, 100)
        progress.isStringPainted = true
        contentPane.add(progress)
        pack()
    }
}