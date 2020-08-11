package sted.ui

import sted.event.FontListChangeEvent
import java.io.File
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class FontKeypad2 : FontKeypad(), ChangeListener {
    override fun loadFont(font: File) {
        val fontMap = fontMap
        fontMap?.setFont2(font)
    }

    override fun setCurrentFont() {
        super.setCurrentFont(fontMap?.font2)
    }

    override fun setCurrentFont(fontName: String) {
        super.setCurrentFont(fontName)
        val fontMap = fontMap
        fontMap?.setFont2(currentFont!!)
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    override fun stateChanged(e: ChangeEvent) {
        if ((e as FontListChangeEvent).fontIndex == 2) {
            fontSelector.stateChanged(e)
            updateUI()
        }
    }
}