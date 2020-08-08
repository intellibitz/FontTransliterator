package sted.actions

import sted.event.FontMapChangeEvent
import java.awt.event.ActionEvent
import javax.swing.JOptionPane
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent

class DeleteAction : CutAction() {
    override fun valueChanged(e: ListSelectionEvent) {
        val listSelectionModel = e.source as ListSelectionModel
        isEnabled = listSelectionModel.minSelectionIndex >= 0
    }

    override fun actionPerformed(e: ActionEvent) {
        val fontMap = stedWindow.desktop.fontMap
        val entries = delete()
        pushUndo(entries!!, fontMap.entries.undo)
        fontMap.isDirty = !entries.isEmpty()
        fontMap.fireUndoEvent()
        fireStatusPosted("Deleted")
    }

    private fun delete(): Collection<*>? {
        val result = JOptionPane.showConfirmDialog(
            stedWindow, "Do you want to delete selected row(s)", "confirm",
            JOptionPane.OK_CANCEL_OPTION
        )
        return if (result == JOptionPane.OK_OPTION) {
            cut()
        } else null
    }

    override fun stateChanged(e: FontMapChangeEvent) {
        isEnabled = !selectedRows.isEmpty()
    }
}