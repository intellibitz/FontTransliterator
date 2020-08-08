package sted.actions

import sted.fontmap.FontMapEntry
import sted.ui.MappingTableModel
import java.util.*
import javax.swing.JTable
import javax.swing.event.ListSelectionListener
import javax.swing.table.TableModel

abstract class TableRowsSelectAction protected constructor() : STEDWindowAction(), ListSelectionListener {
    private var table: JTable? = null
    val tableModel: TableModel
        get() = table!!.model

    fun setTable(table: JTable?) {
        this.table = table
    }

    fun selectAll() {
        table!!.selectAll()
    }

    val selectedRows: Collection<FontMapEntry>
        get() {
            val row = table!!.selectedRows
            val rows: MutableCollection<FontMapEntry> = ArrayList(row.size)
            for (newVar in row) {
                rows.add((table!!.model as MappingTableModel).getValueAt(newVar))
            }
            return rows
        }

    fun copySelectedRows(): Collection<*> {
        val row = table!!.selectedRows
        val rows: MutableCollection<FontMapEntry> = ArrayList(row.size)
        for (newVar in row) {
            rows.add(
                (table!!.model as MappingTableModel)
                    .getValueAt(newVar).clone() as FontMapEntry
            )
        }
        return rows
    }
}