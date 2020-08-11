package sted.actions

import sted.ui.HelpWindow
import java.awt.event.ActionEvent

class HelpAction : STEDWindowAction() {
    override fun actionPerformed(e: ActionEvent) {
        HelpWindow.instance.isVisible = true
    }
}