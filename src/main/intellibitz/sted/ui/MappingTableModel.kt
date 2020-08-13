package sted.ui

import sted.event.IMessageEventSource
import sted.event.IMessageListener
import sted.event.MessageEvent
import sted.fontmap.FontMap
import sted.fontmap.FontMapEntries
import sted.fontmap.FontMapEntry
import sted.io.Resources.getResource
import java.util.*
import javax.swing.table.AbstractTableModel

class MappingTableModel : AbstractTableModel(), IMessageEventSource {
    private val messageEvent = MessageEvent(this)
    lateinit var fontMap: FontMap
    private lateinit var fontMapEntries: FontMapEntries
    private val entries: MutableList<FontMapEntry> = ArrayList()
    private var messageListener: IMessageListener? = null
    override fun getRowCount(): Int {
        try {
            return fontMapEntries.size()
        } catch (e: UninitializedPropertyAccessException) {
            return 0
        }
    }

    override fun getValueAt(row: Int, col: Int): Any {
        if (col == 1) {
            return "="
        }
        if (entries.isNotEmpty()) {
            val entry = entries[row]
            when (col) {
                0 -> {
                    return entry.from
                }
                2 -> {
                    return entry.to
                }
                3 -> {
                    return if (entry.isBeginsWith) {
                        java.lang.Boolean.TRUE
                    } else java.lang.Boolean.FALSE
                }
                4 -> {
                    return if (entry.isEndsWith) {
                        java.lang.Boolean.TRUE
                    } else java.lang.Boolean.FALSE
                }
                5 -> {
                    val `val` = entry.followedBy
                    return `val` ?: ""
                }
                6 -> {
                    val `val` = entry.precededBy
                    return `val` ?: ""
                }
                else -> return "="
            }
        }
        return "="
    }

    fun getValueAt(row: Int): FontMapEntry? {
        return if (entries.isNotEmpty()) {
            entries[row]
        } else null
    }

    override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
        val entry = entries[rowIndex]
        when (columnIndex) {
            0 -> if (entry.from != aValue) {
                val edited = entry.clone() as FontMapEntry
                edited.from = aValue.toString()
                // do a strick check - just like add
                if (!fontMapEntries.isValid(edited)) {
                    fireAlreadyMapped()
                    return
                }
                edited.from = entry.from
                edited.setStatus(2)
                fontMapEntries.undo.add(edited)
                entry.from = aValue.toString()
                entry.setStatus(2)
                fontMapEntries.reKey(edited, entry)
                fontMap.isDirty = true
                super.fireTableCellUpdated(rowIndex, columnIndex)
            }
            2 -> if (entry.to != aValue) {
                val edited = entry.clone() as FontMapEntry
                edited.to = aValue.toString()
                if (fontMapEntries.isValidEdit(edited)) {
                    fireAlreadyMapped()
                    return
                }
                edited.to = entry.to
                edited.setStatus(2)
                fontMapEntries.undo.add(edited)
                entry.to = aValue.toString()
                entry.setStatus(2)
                fontMapEntries.reKey(edited, entry)
                fontMap.isDirty = true
                super.fireTableCellUpdated(rowIndex, columnIndex)
            }
            3 -> {
                val begins = aValue as Boolean
                if (!entry.isBeginsWith == begins) {
                    val edited = entry.clone() as FontMapEntry
                    edited.isBeginsWith = begins
                    if (fontMapEntries.isValidEdit(edited)) {
                        fireAlreadyMapped()
                        return
                    }
                    edited.isBeginsWith = entry.isBeginsWith
                    edited.setStatus(2)
                    fontMapEntries.undo.add(edited)
                    entry.isBeginsWith = begins
                    entry.setStatus(2)
                    fontMapEntries.reKey(edited, entry)
                    fontMap.isDirty = true
                    super.fireTableCellUpdated(rowIndex, columnIndex)
                }
            }
            4 -> {
                val ends = aValue as Boolean
                if (!entry.isEndsWith == ends) {
                    val edited = entry.clone() as FontMapEntry
                    edited.isEndsWith = ends
                    if (fontMapEntries.isValidEdit(edited)) {
                        fireAlreadyMapped()
                        return
                    }
                    edited.isEndsWith = entry.isEndsWith
                    edited.setStatus(2)
                    fontMapEntries.undo.add(edited)
                    entry.isEndsWith = ends
                    entry.setStatus(2)
                    fontMapEntries.reKey(edited, entry)
                    fontMap.isDirty = true
                    super.fireTableCellUpdated(rowIndex, columnIndex)
                }
            }
            5 -> if (aValue != entry.followedBy) {
                if (entry.followedBy == null && "" == aValue) {
                    return
                }
                val edited = entry.clone() as FontMapEntry
                edited.followedBy = aValue.toString()
                if (fontMapEntries.isValidEdit(edited)) {
                    fireAlreadyMapped()
                    return
                }
                edited.followedBy = entry.followedBy
                edited.setStatus(2)
                fontMapEntries.undo.add(edited)
                entry.followedBy = aValue.toString()
                entry.setStatus(2)
                fontMapEntries.reKey(edited, entry)
                fontMap.isDirty = true
                super.fireTableCellUpdated(rowIndex, columnIndex)
            }
            6 -> if (aValue != entry.precededBy) {
                if (entry.precededBy == null && "" == aValue) {
                    return
                }
                val edited = entry.clone() as FontMapEntry
                edited.precededBy = aValue.toString()
                if (fontMapEntries.isValidEdit(edited)) {
                    fireAlreadyMapped()
                    return
                }
                edited.precededBy = entry.precededBy
                edited.setStatus(2)
                fontMapEntries.undo.add(edited)
                entry.precededBy = aValue.toString()
                entry.setStatus(2)
                fontMapEntries.reKey(edited, entry)
                fontMap.isDirty = true
                super.fireTableCellUpdated(rowIndex, columnIndex)
            }
        }
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    override fun getColumnClass(c: Int): Class<*> {
        return getValueAt(0, c).javaClass
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    override fun isCellEditable(row: Int, col: Int): Boolean {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return col != 1
    }

    override fun getColumnCount(): Int {
        return names.size
    }

    override fun getColumnName(column: Int): String {
        return names[column] ?: ""
    }

    fun loadFontMapEntries(fontMap: FontMap) {
        this.fontMap = fontMap
        fontMapEntries = fontMap.entries
        entries.clear()
        entries.addAll(fontMapEntries.values())
        Collections.sort(entries, Collections.reverseOrder())
        super.fireTableDataChanged()
    }

    private fun fireAlreadyMapped() {
        messageEvent.message = "Already mapped - Invalid Edit"
        fireMessagePosted()
    }

    override fun fireMessagePosted() {
        messageListener?.messagePosted(messageEvent)
    }

    override fun addMessageListener(messageListener: IMessageListener) {
        this.messageListener = messageListener
    }

    companion object {
        private var names = arrayOf(
            getResource("title.table.column.symbol1"),
            getResource("title.table.column.equals"),
            getResource("title.table.column.symbol2"),
            getResource("title.table.column.letter1"),
            getResource("title.table.column.letter2"),
            getResource("title.table.column.followed"),
            getResource("title.table.column.preceded")
        )
    }
}