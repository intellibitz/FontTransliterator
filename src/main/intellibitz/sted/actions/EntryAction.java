package intellibitz.sted.actions;

import intellibitz.sted.fontmap.FontMap;
import intellibitz.sted.fontmap.FontMapEntries;
import intellibitz.sted.fontmap.FontMapEntry;
import intellibitz.sted.ui.MappingEntryPanel;
import intellibitz.sted.ui.STEDWindow;
import intellibitz.sted.ui.TabDesktop;
import intellibitz.sted.util.Resources;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class EntryAction
        extends STEDWindowAction {
    private MappingEntryPanel mappingEntryPanel;

    public EntryAction() {
        super();
    }

    public void setFontEntryPanel(MappingEntryPanel mappingEntryPanel) {
        this.mappingEntryPanel = mappingEntryPanel;
    }

    public void keyReleased(KeyEvent e) {
        if (KeyEvent.VK_ENTER == e.getKeyCode()) {
            addFontMapEntry();
        }
    }

    public void actionPerformed(ActionEvent e) {
        addFontMapEntry();
    }

    private void addFontMapEntry() {
        final String key1 = mappingEntryPanel.getWord1().getText();
        final String key2 = mappingEntryPanel.getWord2().getText();
        final STEDWindow stedWindow = getSTEDWindow();
        if (key1 != null && key2 != null && key1.length() > 0 &&
                key2.length() > 0) {
            final TabDesktop tabDesktop =
                    stedWindow.getDesktop();
            final FontMap fontMap = tabDesktop.getFontMap();
            final FontMapEntries entries = fontMap.getEntries();
            final FontMapEntry fontMapEntry = new FontMapEntry(key1, key2);
            if (entries.add(fontMapEntry)) {
                // add it to the undo list too
                fontMapEntry.setStatus(Resources.ENTRY_STATUS_ADD);
                entries.getUndo().push(fontMapEntry);
                fontMap.setDirty(true);
                fontMap.fireUndoEvent();
                tabDesktop.getDesktopModel()
                        .fireFontMapChangedEvent();
                fireStatusPosted(
                        "New FontMap Entry Added");
            } else {
                fireMessagePosted("Already mapped.. Invalid Add");
            }
        } else {
            fireMessagePosted(
                    "Insufficient data.. Please use both keypads to map characters ");
        }
    }

}
