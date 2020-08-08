package sted.actions

import javax.swing.AbstractAction
import sted.fontmap.Converter
import java.awt.event.ActionEvent

class TransliterateStopAction : AbstractAction() {
    private var converter: Converter? = null
    override fun actionPerformed(e: ActionEvent) {
        stop()
        isEnabled = false
    }

    fun setConverter(converter: Converter?) {
        this.converter = converter
    }

    private fun stop() {
        if (converter != null && converter!!.isAlive) {
            converter!!.setStopRequested(true)
            converter!!.interrupt()
            converter!!.setSuccess(false)
            converter!!.message = "Stopped Conversion"
            converter!!.fireThreadRunFinished()
        }
    }
}