package sted.actions

import sted.fontmap.FontMapEntry
import sted.ui.MappingTableModel
import java.util.*
import javax.swing.JTable
import javax.swing.event.ListSelectionListener

abstract class TableRowsSelectAction protected constructor() : STEDWindowAction(), ListSelectionListener {
    lateinit var table: JTable
    val tableModel: MappingTableModel
        get() = table.model as MappingTableModel

    fun selectAll() {
        table.selectAll()
    }

    val selectedRows: Collection<FontMapEntry>
        get() {
            val row = table.selectedRows
            val rows: MutableCollection<FontMapEntry> = ArrayList(row.size)
            for (newVar in row) {
                rows.add(tableModel.getValueAt(newVar)!!)
            }
            return rows
        }

    fun copySelectedRows(): Collection<*> {
        val row = table.selectedRows
        val rows: MutableCollection<FontMapEntry> = ArrayList(row.size)
        for (newVar in row) {
            rows.add(
                tableModel.getValueAt(newVar)?.clone() as FontMapEntry
            )
        }
        return rows
    }
}