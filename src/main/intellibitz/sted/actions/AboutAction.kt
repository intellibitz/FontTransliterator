package sted.actions

import sted.ui.AboutSTED.Companion.instance
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

class AboutAction : AbstractAction() {
    override fun actionPerformed(e: ActionEvent) {
        instance.isVisible = true
    }
}