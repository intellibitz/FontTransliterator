package intellibitz.sted.actions;

import intellibitz.sted.ui.MappingEntryPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EntryClearAction
        extends AbstractAction {
    private MappingEntryPanel mappingPreviewPanel;

    public EntryClearAction() {
        super();
    }

    public void setFontPreviewPanel(MappingEntryPanel mappingPreviewPanel) {
        this.mappingPreviewPanel = mappingPreviewPanel;
    }

    private MappingEntryPanel getFontPreviewPanel() {
        return mappingPreviewPanel;
    }

    public void actionPerformed(ActionEvent e) {
        final MappingEntryPanel mappingPreviewPanel = getFontPreviewPanel();
        mappingPreviewPanel.clearPreviewDisplay();
        mappingPreviewPanel.getClearButton().setEnabled(false);
    }

}
