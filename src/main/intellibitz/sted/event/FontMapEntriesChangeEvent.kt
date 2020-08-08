package sted.event

import sted.fontmap.FontMapEntries
import javax.swing.event.ChangeEvent

class FontMapEntriesChangeEvent(fontMapEntries: FontMapEntries) : ChangeEvent(fontMapEntries) {
    val fontMapEntries: FontMapEntries
        get() = getSource() as FontMapEntries
}