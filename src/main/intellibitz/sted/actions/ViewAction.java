package intellibitz.sted.actions;

import intellibitz.sted.util.MenuHandler;
import intellibitz.sted.util.Resources;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class ViewAction
        extends ItemListenerAction {
    public ViewAction() {
        super();
    }

    static public class ViewSample
            extends ViewAction {
        public ViewSample() {
            super();
        }

        public void itemStateChanged(ItemEvent e) {
            final JPanel sampleText = getSTEDWindow().getDesktop()

                    .getFontMapperDesktopFrame()
                    .getMapperPanel()
                    .getPreviewPanel();
            sampleText.setVisible(ItemEvent.SELECTED == e.getStateChange());
            sampleText.validate();
        }
    }

    static public class ViewToolBar
            extends ViewAction {
        public ViewToolBar() {
            super();
        }

        public void itemStateChanged(ItemEvent e) {
            MenuHandler.getInstance().getToolBar(Resources.MENUBAR_STED)
                    .setVisible(
                            ItemEvent.SELECTED == e.getStateChange());
        }
    }

    static public class ViewStatus
            extends ViewAction {
        public ViewStatus() {
            super();
        }

        public void itemStateChanged(ItemEvent e) {
            getSTEDWindow().getStatusPanel()
                    .setVisible(ItemEvent.SELECTED == e.getStateChange());
        }
    }

    static public class ViewMapping
            extends ViewAction {
        public ViewMapping() {
            super();
        }

        public void itemStateChanged(ItemEvent e) {
            final JSplitPane splitPane = getSTEDWindow()
                    .getDesktop()
                    .getFontMapperDesktopFrame()
                    .getMapperPanel
                            ().getMappingEntryPanel().getSplitPane();
            splitPane.getBottomComponent().setVisible
                    (ItemEvent.SELECTED == e.getStateChange());
            splitPane.resetToPreferredSizes();
            splitPane.validate();
        }
    }

}
