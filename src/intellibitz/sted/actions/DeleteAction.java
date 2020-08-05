package intellibitz.sted.actions;

import intellibitz.sted.event.FontMapChangeEvent;
import intellibitz.sted.fontmap.FontMap;
import intellibitz.sted.ui.STEDWindow;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.util.Collection;

public class DeleteAction
        extends CutAction {
    public DeleteAction() {
        super();
    }


    public void valueChanged(ListSelectionEvent e) {
        final ListSelectionModel listSelectionModel =
                (ListSelectionModel) e.getSource();
        setEnabled(listSelectionModel.getMinSelectionIndex() >= 0);
    }

    public void actionPerformed(ActionEvent e) {
        final FontMap fontMap =
                getSTEDWindow().getDesktop()
                        .getFontMap();
        final Collection entries = delete();
        pushUndo(entries, fontMap.getEntries().getUndo());
        fontMap.setDirty(!entries.isEmpty());
        fontMap.fireUndoEvent();
        fireStatusPosted("Deleted");
    }

    private Collection delete() {
        final STEDWindow stedWindow = getSTEDWindow();
        final int result = JOptionPane.showConfirmDialog
                (stedWindow, "Do you want to delete selected row(s)", "confirm",
                        JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return cut();
        }
        return null;
    }


    public void stateChanged(FontMapChangeEvent e) {
        setEnabled(!getSelectedRows().isEmpty());
    }

}
