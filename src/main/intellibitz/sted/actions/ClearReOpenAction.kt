package sted.actions

import sted.ui.MenuHandler
import java.awt.event.ActionEvent

class ClearReOpenAction : STEDWindowAction() {
    override fun actionPerformed(e: ActionEvent) {
        MenuHandler.clearReOpenItems()
    }
}