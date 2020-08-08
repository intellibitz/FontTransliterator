package sted.actions

import sted.ui.MappingEntryPanel
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

class EntryClearAction : AbstractAction() {
    lateinit var mappingEntryPanel: MappingEntryPanel

    override fun actionPerformed(e: ActionEvent) {
        mappingEntryPanel.clearPreviewDisplay()
        mappingEntryPanel.clearButton?.isEnabled = false
    }
}