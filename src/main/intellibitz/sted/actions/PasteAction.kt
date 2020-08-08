package sted.actions

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.fontmap.FontMapEntry
import sted.io.Resources
import sted.ui.MappingTableModel
import java.awt.event.ActionEvent
import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableModelEvent

class PasteAction : TableModelListenerAction(), FontMapChangeListener {
    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        isEnabled = !stedWindow.desktop
            .getClipboard()
            .isEmpty()
    }

    override fun valueChanged(e: ListSelectionEvent) {
        isEnabled = !stedWindow.desktop
            .getClipboard()
            .isEmpty()
    }

    override fun actionPerformed(e: ActionEvent) {
        paste()
        fireStatusPosted(Resources.ACTION_PASTE_COMMAND)
    }

    override fun stateChanged(e: FontMapChangeEvent?) {
        isEnabled = !stedWindow.desktop
            .getClipboard()
            .isEmpty()
    }

    private fun paste() {
        val entries = stedWindow
            .desktop.clipboard[Resources.ENTRIES]
        if (entries != null && !entries.isEmpty()) {
            val fontMap = stedWindow.desktop
                .fontMap
            val fontMapEntries = fontMap.entries
            var flag = false
            for (newVar in entries) {
                val entry = newVar as FontMapEntry
                if (entry != null &&
                    fontMapEntries.add(entry.clone() as FontMapEntry)
                ) {
                    flag = true
                }
            }
            fontMap.isDirty = flag
            if (fontMap.isNew) {
                stedWindow.desktop
                    .fontMapperDesktopFrame
                    .mapperPanel.mappingEntryPanel.setFontMap(fontMap)
            } else {
                (tableModel as MappingTableModel).fontMap = fontMap
            }
        }
    }
}