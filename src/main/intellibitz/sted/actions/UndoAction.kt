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

class UndoAction : TableModelListenerAction(), FontMapChangeListener, ChangeListener {
    override fun actionPerformed(e: ActionEvent) {
        undo(stedWindow)
        fireStatusPosted("Undo")
        val fontMap: FontMap = stedWindow.desktop
            .getFontMap()
        fontMap.fireUndoEvent()
        fontMap.fireRedoEvent()
    }

    fun undo(stedWindow: STEDWindow) {
        val fontMap = stedWindow.desktop
            .fontMap
        val fontMapEntries = fontMap.entries
        val undoEntries = fontMapEntries.undo
        if (undoEntries.isEmpty()) {
            return
        }
        val fontMapEntry = undoEntries.pop()
        if (fontMapEntry.isAdded) {
            val current = fontMapEntries.remove(fontMapEntry.id)
            // change the status when pushing to the redo stack
            current.setStatus(Resources.ENTRY_STATUS_DELETE)
            fontMapEntries.redo.push(current)
        } else if (fontMapEntry.isEdited) {
            val current = fontMapEntries.remove(fontMapEntry.id)
            fontMapEntries.redo.push(current)
            fontMapEntries.add(fontMapEntry)
        } else if (fontMapEntry.isDeleted) {
            // change the status when pushing to the redo stack
            fontMapEntry.setStatus(Resources.ENTRY_STATUS_ADD)
            fontMapEntries.add(fontMapEntry)
            fontMapEntries.redo.push(fontMapEntry)
        }
        stedWindow.desktop
            .desktopModel.fireFontMapChangedEvent()
    }

    private fun setEnabled(fontMap: FontMap): Boolean {
        val empty = fontMap.entries.undo.isEmpty()
        isEnabled = !empty
        return !empty
    }

    override fun stateChanged(e: FontMapChangeEvent?) {
        val fontMap = e!!.fontMap
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
                .getFontMap()
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
            setEnabled(dframe.model.fontMap!!)
        }
    }
}