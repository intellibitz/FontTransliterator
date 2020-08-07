package sted.ui

import sted.io.Resources
import javax.swing.JTextPane
import javax.swing.text.html.HTMLEditorKit

/**
 * sets HTMLEditorKit. reads Resources for the text to be displayed. Singleton.
 */
class AboutText private constructor() : JTextPane() {
    companion object {
        private var aboutText: AboutText? = null

        @JvmStatic
        @get:Synchronized
        val instance: AboutText?
            get() {
                if (aboutText == null) {
                    aboutText = AboutText()
                }
                return aboutText
            }
    }

    init {
        isEditable = false
        setSize(400, 400)
        editorKit = HTMLEditorKit()
        text = Resources.getResource("about.dialog.text")
    }
}