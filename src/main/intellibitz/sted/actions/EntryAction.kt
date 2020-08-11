package sted.actions

import sted.fontmap.FontMapEntry
import sted.io.Resources
import sted.ui.MappingEntryPanel
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent

class EntryAction : STEDWindowAction() {
    lateinit var mappingEntryPanel: MappingEntryPanel

    override fun keyReleased(e: KeyEvent) {
        if (KeyEvent.VK_ENTER == e.keyCode) {
            addFontMapEntry()
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        addFontMapEntry()
    }

    private fun addFontMapEntry() {
        val key1 = mappingEntryPanel.word1.text
        val key2 = mappingEntryPanel.word2.text
        if (!key1.isNullOrEmpty() && !key2.isNullOrBlank()) {
            val tabDesktop = stedWindow.desktop
            val fontMap = tabDesktop.fontMap
            val entries = fontMap.entries
            val fontMapEntry = FontMapEntry()
            fontMapEntry.init(key1, key2)
            if (entries.add(fontMapEntry)) {
                // add it to the undo list too
                fontMapEntry.setStatus(Resources.ENTRY_STATUS_ADD)
                entries.undo.push(fontMapEntry)
                fontMap.isDirty = true
                fontMap.fireUndoEvent()
                tabDesktop.desktopModel
                    .fireFontMapChangedEvent()
                fireStatusPosted(
                    "New FontMap Entry Added"
                )
            } else {
                fireMessagePosted("Already mapped.. Invalid Add")
            }
        } else {
            fireMessagePosted(
                "Insufficient data.. Please use both keypads to map characters "
            )
        }
    }
}