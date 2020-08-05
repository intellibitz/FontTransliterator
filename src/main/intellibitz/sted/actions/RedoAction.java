package intellibitz.sted.actions;

import intellibitz.sted.event.FontMapChangeEvent;
import intellibitz.sted.event.FontMapChangeListener;
import intellibitz.sted.fontmap.FontMap;
import intellibitz.sted.fontmap.FontMapEntries;
import intellibitz.sted.fontmap.FontMapEntry;
import intellibitz.sted.ui.DesktopFrame;
import intellibitz.sted.ui.STEDWindow;
import intellibitz.sted.ui.TabDesktop;
import intellibitz.sted.util.Resources;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.util.Stack;

public class RedoAction
        extends TableModelListenerAction
        implements FontMapChangeListener,
        ChangeListener {
    public RedoAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {

        redo(getSTEDWindow());
        final FontMap fontMap =
                getSTEDWindow().getDesktop()
                        .getFontMap();
        fontMap.setDirty(true);
        fireStatusPosted("Redo");
        fontMap.fireUndoEvent();
        fontMap.fireRedoEvent();
    }

    public void redo(STEDWindow stedWindow) {
        final FontMapEntries fontMapEntries =
                stedWindow.getDesktop()
                        .getFontMap().getEntries();
        final Stack<FontMapEntry> redoEntries = fontMapEntries.getRedo();
        if (redoEntries.isEmpty()) {
            return;
        }
        final FontMapEntry fontMapEntry = redoEntries.pop();
        if (fontMapEntry.isAdded()) {
            final FontMapEntry current =
                    fontMapEntries.remove(fontMapEntry.getId());
            // change the status when pushing to the redo stack
            current.setStatus(Resources.ENTRY_STATUS_DELETE);
            fontMapEntries.getUndo().push(current);
        } else if (fontMapEntry.isEdited()) {
            final FontMapEntry current =
                    fontMapEntries.remove(fontMapEntry.getId());
            fontMapEntries.getUndo().push(current);
            fontMapEntries.add(fontMapEntry);
        } else if (fontMapEntry.isDeleted()) {
            // change the status when pushing to the redo stack
            fontMapEntry.setStatus(Resources.ENTRY_STATUS_ADD);
            fontMapEntries.add(fontMapEntry);
            fontMapEntries.getUndo().push(fontMapEntry);
        }
        stedWindow.getDesktop()
                .getDesktopModel().fireFontMapChangedEvent();
    }

    private boolean setEnabled(FontMap fontMap) {
        boolean empty = fontMap.getEntries().getRedo().isEmpty();
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
