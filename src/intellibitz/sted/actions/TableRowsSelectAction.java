package intellibitz.sted.actions;

import intellibitz.sted.fontmap.FontMapEntry;
import intellibitz.sted.ui.MappingTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collection;

abstract public class TableRowsSelectAction
        extends STEDWindowAction
        implements ListSelectionListener {
    private JTable table;

    protected TableRowsSelectAction() {
        super();
    }

    TableModel getTableModel() {
        return table.getModel();
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    void selectAll() {
        table.selectAll();
    }

    Collection<FontMapEntry> getSelectedRows() {
        final int row[] = table.getSelectedRows();
        final Collection<FontMapEntry> rows =
                new ArrayList<FontMapEntry>(row.length);
        for (final int newVar : row) {
            rows.add(((MappingTableModel) table.getModel()).getValueAt(newVar));
        }
        return rows;
    }

    Collection copySelectedRows() {
        final int row[] = table.getSelectedRows();
        final Collection<FontMapEntry> rows =
                new ArrayList<FontMapEntry>(row.length);
        for (final int newVar : row) {
            rows.add((FontMapEntry) ((MappingTableModel) table.getModel())
                    .getValueAt(newVar).clone());
        }
        return rows;
    }
}
