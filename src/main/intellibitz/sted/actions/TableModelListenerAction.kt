package sted.actions

import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

abstract class TableModelListenerAction protected constructor() : TableRowsSelectAction(), TableModelListener {
    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        isEnabled = (e.source as TableModel).rowCount > 0
    }

    override fun valueChanged(e: ListSelectionEvent) {}
}