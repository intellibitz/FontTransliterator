package sted.event

import sted.actions.TableRowsSelectAction
import sted.ui.MenuHandler
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPopupMenu
import javax.swing.JTable

class MappingPopupListener : MouseAdapter() {
    private var popupMenu: JPopupMenu = MenuHandler.popupMenu
    fun load() {
        popupMenu = MenuHandler.popupMenu
    }

    override fun mousePressed(e: MouseEvent) {
        maybeShowPopup(e)
    }

    override fun mouseReleased(e: MouseEvent) {
        maybeShowPopup(e)
    }

    private fun maybeShowPopup(e: MouseEvent) {
        if (e.isPopupTrigger) {
            setTableOnAction(e.source as JTable)
            popupMenu.show(e.component, e.x, e.y)
        }
    }

    private fun setTableOnAction(table: JTable) {
        for (action in MenuHandler.actions.values) {
            if (action is TableRowsSelectAction) {
                action.table = table
            }
        }
    }
}