package sted.actions

import java.awt.event.ActionEvent
import javax.swing.event.ListSelectionEvent

class SelectAllAction : TableModelListenerAction() {
    override fun valueChanged(e: ListSelectionEvent) {}
    override fun actionPerformed(e: ActionEvent) {
        super.selectAll()
        fireStatusPosted("Selected All")
    }
}