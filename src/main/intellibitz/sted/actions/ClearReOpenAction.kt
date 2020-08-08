package sted.actions

import sted.ui.MenuHandler.Companion.clearReOpenItems
import sted.ui.MenuHandler.Companion.instance
import java.awt.event.ActionEvent

class ClearReOpenAction : STEDWindowAction() {
    override fun actionPerformed(e: ActionEvent) {
        clearReOpenItems(instance)
    }
}