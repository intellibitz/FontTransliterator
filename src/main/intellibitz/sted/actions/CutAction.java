package sted.actions;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.fontmap.FontMap;
import sted.fontmap.FontMapEntry;
import sted.ui.DesktopModel;
import sted.ui.STEDWindow;
import sted.util.Resources;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Stack;

public class CutAction
        extends TableModelListenerAction
        implements FontMapChangeListener {
    public CutAction() {
        super();
    }


    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
    }

    public void valueChanged(ListSelectionEvent e) {
        setEnabled(((ListSelectionModel) e.getSource())
                .getMinSelectionIndex() >= 0);
    }

    public void actionPerformed(ActionEvent e) {
        final Collection entries = cut();
        final STEDWindow stedWindow = getSTEDWindow();
        stedWindow.getDesktop()
                .addToClipboard(Resources.ENTRIES, entries);
        final FontMap fontMap = stedWindow.getDesktop()
                .getFontMap();
        pushUndo(entries, fontMap.getEntries().getUndo());
        fontMap.setDirty(!entries.isEmpty());
        fontMap.fireUndoEvent();
        fireStatusPosted("Cut");
    }

    public void pushUndo(Collection entries, Stack<FontMapEntry> undo) {
        for (Object entry : entries) {
            final FontMapEntry fontMapEntry = (FontMapEntry) entry;
            fontMapEntry.setStatus(Resources.ENTRY_STATUS_DELETE);
            undo.push(fontMapEntry);
        }
    }

    Collection cut() {
        final STEDWindow stedWindow = getSTEDWindow();
        DesktopModel desktopModel =
                stedWindow.getDesktop()
                        .getDesktopModel();
        final FontMap fontMap = desktopModel.getFontMap();
        final Collection entries =
                fontMap.getEntries().remove(getSelectedRows());
//        stedWindow.addListenersToDesktopFrame(fontMap);
//        desktopModel.fireFontMapChangedEvent();
        return entries;
    }

    public void stateChanged(FontMapChangeEvent e) {
        setEnabled(!getSelectedRows().isEmpty());
    }

}
