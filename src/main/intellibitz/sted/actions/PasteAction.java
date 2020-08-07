package sted.actions;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.fontmap.FontMap;
import sted.fontmap.FontMapEntries;
import sted.fontmap.FontMapEntry;
import sted.ui.MappingTableModel;
import sted.ui.STEDWindow;
import sted.util.Resources;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.util.Collection;

public class PasteAction
        extends TableModelListenerAction
        implements FontMapChangeListener {
    public PasteAction() {
        super();
    }


    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        setEnabled(!getSTEDWindow().getDesktop()
                .getClipboard()
                .isEmpty());
    }

    public void valueChanged(ListSelectionEvent e) {
        setEnabled(!getSTEDWindow().getDesktop()
                .getClipboard()
                .isEmpty());
    }

    public void actionPerformed(ActionEvent e) {
        paste();
        fireStatusPosted(Resources.ACTION_PASTE_COMMAND);
    }

    public void stateChanged(FontMapChangeEvent e) {
        setEnabled(!getSTEDWindow().getDesktop()
                .getClipboard()
                .isEmpty());
    }

    private void paste() {
        final STEDWindow stedWindow = getSTEDWindow();
        final Collection entries = stedWindow
                .getDesktop().getClipboard()
                .get(Resources.ENTRIES);
        if (entries != null && !entries.isEmpty()) {
            final FontMap fontMap =
                    stedWindow.getDesktop()
                            .getFontMap();
            final FontMapEntries fontMapEntries = fontMap.getEntries();
            boolean flag = false;
            for (final Object newVar : entries) {
                final FontMapEntry entry = (FontMapEntry) newVar;
                if (entry != null &&
                        fontMapEntries.add((FontMapEntry) entry.clone())) {
                    flag = true;
                }
            }
            fontMap.setDirty(flag);
            if (fontMap.isNew()) {
                stedWindow.getDesktop()
                        .getFontMapperDesktopFrame()
                        .getMapperPanel().getMappingEntryPanel
                        ().setFontMap(fontMap);
            } else {
                ((MappingTableModel) getTableModel()).setFontMap(fontMap);
            }
        }
    }

}
