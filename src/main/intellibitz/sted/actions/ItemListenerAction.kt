package sted.actions

import sted.ui.MenuHandler.Companion.instance
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.AbstractButton

open class ItemListenerAction : STEDWindowAction(), ItemListener {
    override fun actionPerformed(e: ActionEvent) {}

    /**
     * Invoked when an item has been selected or deselected by the user. The
     * code written for this method performs the operations that need to occur
     * when an item is selected (or deselected).
     */
    override fun itemStateChanged(e: ItemEvent) {
        if (e.source is AbstractButton) {
            val name = (e.source as AbstractButton).action?.getValue(NAME) as String
            val state = !(ItemEvent.DESELECTED == e.stateChange)
            instance.getMenuItem(name)?.isSelected = state
            instance.getToolButton(name)?.isSelected = state
//            val stedWindow = sTEDWindow!!
            //            getSTEDWindow().convertSampleText();
        }
    }
}