package sted.ui

import sted.event.FontListChangeEvent
import java.io.File
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class FontKeypad1 : FontKeypad(), ChangeListener {
    override fun setCurrentFont() {
        super.setCurrentFont(fontMap?.font1)
    }

    override fun loadFont(font: File) {
        val fontMap = fontMap
        fontMap?.setFont1(font)
    }

    override fun setCurrentFont(fontName: String) {
        super.setCurrentFont(fontName)
        val fontMap = fontMap
        fontMap?.setFont1(currentFont!!)
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    override fun stateChanged(e: ChangeEvent) {
        if ((e as FontListChangeEvent).fontIndex == 1) {
            fontSelector.stateChanged(e)
            updateUI()
        }
    }
}