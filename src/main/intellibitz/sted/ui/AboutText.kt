package sted.ui

import sted.io.Resources
import javax.swing.JTextPane
import javax.swing.text.html.HTMLEditorKit

class AboutText private constructor() : JTextPane() {
    companion object {
        val instance: AboutText by lazy {
            AboutText()
        }
    }

    init {
        isEditable = false
        setSize(400, 400)
        editorKit = HTMLEditorKit()
        text = Resources.getResource("about.dialog.text")
    }
}