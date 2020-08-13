package sted.actions

import sted.ui.MenuHandler
import java.awt.event.ItemEvent
import javax.swing.JPanel
import javax.swing.JSplitPane

open class ViewAction : ItemListenerAction() {
    class ViewSample : ViewAction() {
        override fun itemStateChanged(e: ItemEvent) {
            val sampleText: JPanel = stedWindow.desktop
                .fontMapperDesktopFrame
                .mapperPanel
                .previewPanel
            sampleText.isVisible = ItemEvent.SELECTED == e.stateChange
            sampleText.validate()
        }
    }

    class ViewToolBar : ViewAction() {
        override fun itemStateChanged(e: ItemEvent) {
            MenuHandler.toolBars["STED-MenuBar"]?.isVisible = ItemEvent.SELECTED == e.stateChange
        }
    }

    class ViewStatus : ViewAction() {
        override fun itemStateChanged(e: ItemEvent) {
            stedWindow.statusPanel.isVisible = ItemEvent.SELECTED == e.stateChange
        }
    }

    class ViewMapping : ViewAction() {
        override fun itemStateChanged(e: ItemEvent) {
            val splitPane: JSplitPane = stedWindow
                .desktop
                .fontMapperDesktopFrame
                .mapperPanel.mappingEntryPanel.splitPane
            splitPane.bottomComponent.isVisible = ItemEvent.SELECTED == e.stateChange
            splitPane.resetToPreferredSizes()
            splitPane.validate()
        }
    }
}