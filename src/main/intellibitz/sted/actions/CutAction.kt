package sted.actions

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.fontmap.FontMapEntry
import sted.io.Resources
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableModelEvent

open class CutAction : TableModelListenerAction(), FontMapChangeListener {
    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {}
    override fun valueChanged(e: ListSelectionEvent) {
        isEnabled = (e.source as ListSelectionModel)
            .minSelectionIndex >= 0
    }

    override fun actionPerformed(e: ActionEvent) {
        val entries = cut()
        stedWindow.desktop.addToClipboard(Resources.ENTRIES, entries)
        val fontMap = stedWindow.desktop.fontMap
        pushUndo(entries, fontMap.entries.undo)
        fontMap.isDirty = !entries.isEmpty()
        fontMap.fireUndoEvent()
        fireStatusPosted("Cut")
    }

    fun pushUndo(entries: Collection<*>, undo: Stack<FontMapEntry>) {
        for (entry in entries) {
            val fontMapEntry = entry as FontMapEntry
            fontMapEntry.setStatus(Resources.ENTRY_STATUS_DELETE)
            undo.push(fontMapEntry)
        }
    }

    fun cut(): Collection<*> {
        val desktopModel = stedWindow.desktop.desktopModel
        val fontMap = desktopModel.fontMap
        //        stedWindow.addListenersToDesktopFrame(fontMap);
//        desktopModel.fireFontMapChangedEvent();
        return fontMap.entries.remove(selectedRows)
    }

    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        isEnabled = !selectedRows.isEmpty()
    }
}