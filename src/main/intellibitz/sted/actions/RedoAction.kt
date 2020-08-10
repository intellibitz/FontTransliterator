package sted.actions

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.fontmap.FontMap
import sted.io.Resources
import sted.ui.DesktopFrame
import sted.ui.STEDWindow
import sted.ui.TabDesktop
import java.awt.event.ActionEvent
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.TableModelEvent

class RedoAction : TableModelListenerAction(), FontMapChangeListener, ChangeListener {
    override fun actionPerformed(e: ActionEvent) {
        redo(stedWindow)
        val fontMap: FontMap = stedWindow.desktop
            .fontMap
        fontMap.isDirty = true
        fireStatusPosted("Redo")
        fontMap.fireUndoEvent()
        fontMap.fireRedoEvent()
    }

    fun redo(stedWindow: STEDWindow) {
        val fontMapEntries = stedWindow.desktop
            .fontMap.entries
        val redoEntries = fontMapEntries.redo
        if (redoEntries.isEmpty()) {
            return
        }
        val fontMapEntry = redoEntries.pop()
        if (fontMapEntry.isAdded) {
            val current = fontMapEntries.remove(fontMapEntry.id)
            // change the status when pushing to the redo stack
            current.setStatus(Resources.ENTRY_STATUS_DELETE)
            fontMapEntries.undo.push(current)
        } else if (fontMapEntry.isEdited) {
            val current = fontMapEntries.remove(fontMapEntry.id)
            fontMapEntries.undo.push(current)
            fontMapEntries.add(fontMapEntry)
        } else if (fontMapEntry.isDeleted) {
            // change the status when pushing to the redo stack
            fontMapEntry.setStatus(Resources.ENTRY_STATUS_ADD)
            fontMapEntries.add(fontMapEntry)
            fontMapEntries.undo.push(fontMapEntry)
        }
        stedWindow.desktop
            .desktopModel.fireFontMapChangedEvent()
    }

    private fun setEnabled(fontMap: FontMap): Boolean {
        val empty = fontMap.entries.redo.isEmpty()
        isEnabled = !empty
        return !empty
    }

    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        val fontMap = fontMapChangeEvent.fontMap
        if (!setEnabled(fontMap)) {
            fontMap.isDirty = false
        }
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        setEnabled(
            stedWindow.desktop
                .fontMap
        )
    }

    /**
     * This listens for state change in TabDesktop, when tab selection is made
     *
     * @param e
     */
    override fun stateChanged(e: ChangeEvent) {
        val desktop = e.source as TabDesktop
        val index = desktop.selectedIndex
        if (index > -1) {
            val dframe = desktop.getComponentAt(
                index
            ) as DesktopFrame
            setEnabled(dframe.model.fontMap)
        }
    }
}