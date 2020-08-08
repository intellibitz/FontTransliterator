package sted.actions

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.io.Resources
import java.awt.event.ActionEvent
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableModelEvent

class CopyAction : TableModelListenerAction(), FontMapChangeListener {
    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
//        this.setEnabled(!this.getSelectedRows().isEmpty());
    }

    override fun valueChanged(e: ListSelectionEvent) {
        val listSelectionModel = e.source as ListSelectionModel
        isEnabled = listSelectionModel.minSelectionIndex >= 0
    }

    override fun actionPerformed(e: ActionEvent) {
        stedWindow.desktop.addToClipboard(Resources.ENTRIES, copySelectedRows())
    }

    override fun stateChanged(e: FontMapChangeEvent) {
        isEnabled = !selectedRows.isEmpty()
    }
}