package intellibitz.sted.actions;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

abstract public class TableModelListenerAction
        extends TableRowsSelectAction
        implements TableModelListener {
    protected TableModelListenerAction() {
        super();
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        setEnabled(((TableModel) e.getSource()).getRowCount() > 0);
    }

    public void valueChanged(ListSelectionEvent e) {
    }

}
