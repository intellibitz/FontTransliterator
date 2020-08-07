package sted.actions;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.fontmap.FontMap;
import sted.fontmap.FontMapEntries;
import sted.fontmap.FontMapEntry;
import sted.ui.DesktopFrame;
import sted.ui.STEDWindow;
import sted.ui.TabDesktop;
import sted.util.Resources;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.util.Stack;

public class UndoAction
        extends TableModelListenerAction
        implements FontMapChangeListener,
        ChangeListener {
    public UndoAction() {
        super();
    }


    public void actionPerformed(ActionEvent e) {

        undo(getSTEDWindow());
        fireStatusPosted("Undo");
        final FontMap fontMap =
                getSTEDWindow().getDesktop()
                        .getFontMap();
        fontMap.fireUndoEvent();
        fontMap.fireRedoEvent();
    }

    public void undo(STEDWindow stedWindow) {
        final FontMap fontMap = stedWindow.getDesktop()
                .getFontMap();
        final FontMapEntries fontMapEntries = fontMap.getEntries();
        final Stack<FontMapEntry> undoEntries = fontMapEntries.getUndo();
        if (undoEntries.isEmpty()) {
            return;
        }
        final FontMapEntry fontMapEntry = undoEntries.pop();
        if (fontMapEntry.isAdded()) {
            final FontMapEntry current =
                    fontMapEntries.remove(fontMapEntry.getId());
            // change the status when pushing to the redo stack
            current.setStatus(Resources.ENTRY_STATUS_DELETE);
            fontMapEntries.getRedo().push(current);
        } else if (fontMapEntry.isEdited()) {
            final FontMapEntry current =
                    fontMapEntries.remove(fontMapEntry.getId());
            fontMapEntries.getRedo().push(current);
            fontMapEntries.add(fontMapEntry);
        } else if (fontMapEntry.isDeleted()) {
            // change the status when pushing to the redo stack
            fontMapEntry.setStatus(Resources.ENTRY_STATUS_ADD);
            fontMapEntries.add(fontMapEntry);
            fontMapEntries.getRedo().push(fontMapEntry);
        }
        stedWindow.getDesktop()
                .getDesktopModel().fireFontMapChangedEvent();
    }

    private boolean setEnabled(FontMap fontMap) {
        boolean empty = fontMap.getEntries().getUndo().isEmpty();
        setEnabled(!empty);
        return !empty;
    }

    public void stateChanged(FontMapChangeEvent e) {
        final FontMap fontMap = e.getFontMap();
        if (!setEnabled(fontMap)) {
            fontMap.setDirty(false);
        }
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        setEnabled(getSTEDWindow().getDesktop()
                .getFontMap());
    }

    /**
     * This listens for state change in TabDesktop, when tab selection is made
     *
     * @param e
     */
    public void stateChanged(ChangeEvent e) {
        TabDesktop desktop = (TabDesktop) e.getSource();
        int index = desktop.getSelectedIndex();
        if (index > -1) {
            DesktopFrame dframe =
                    (DesktopFrame) desktop.getComponentAt(
                            index);
            setEnabled(dframe.getModel().getFontMap());
        }

    }
}
