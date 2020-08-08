package sted.actions

import sted.io.FileHelper.openFont
import sted.io.Resources
import sted.io.Resources.getSetting
import sted.ui.FontKeypad
import java.awt.event.ActionEvent

class LoadFontAction(fontSelectPanel: FontKeypad) : STEDWindowAction() {
    private val fontKeypad: FontKeypad

    /**
     * Invoked when an action occurs.
     */
    override fun actionPerformed(e: ActionEvent) {
        loadFont()
    }

    private fun loadFont() {
        val file = openFont(stedWindow)
        if (file != null) {
            fontKeypad.loadFont(file)
        }
    }

    init {
        putValue(NAME, getSetting(Resources.LABEL_FONT_LOAD))
        fontKeypad = fontSelectPanel
    }
}