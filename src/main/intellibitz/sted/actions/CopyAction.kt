package sted.actions;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.ui.STEDWindow;
import sted.io.Resources;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;

public class CopyAction
        extends TableModelListenerAction
        implements FontMapChangeListener {
    public CopyAction() {
        super();
    }


    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
//        this.setEnabled(!this.getSelectedRows().isEmpty());
    }

    public void valueChanged(ListSelectionEvent e) {
        final ListSelectionModel listSelectionModel =
                (ListSelectionModel) e.getSource();
        setEnabled(listSelectionModel.getMinSelectionIndex() >= 0);
    }

    public void actionPerformed(ActionEvent e) {
        final STEDWindow stedWindow = getSTEDWindow();
        stedWindow.getDesktop()
                .addToClipboard(Resources.ENTRIES, copySelectedRows());
    }

    public void stateChanged(FontMapChangeEvent e) {
        setEnabled(!getSelectedRows().isEmpty());
    }

}