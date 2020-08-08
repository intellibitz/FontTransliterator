package sted.actions

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.ui.TabDesktop
import java.awt.event.ActionEvent
import javax.swing.event.InternalFrameEvent
import javax.swing.event.InternalFrameListener

class NewFontMapAction : STEDWindowAction(), FontMapChangeListener, InternalFrameListener {
    override fun actionPerformed(e: ActionEvent) {
        val tabDesktop: TabDesktop = stedWindow.desktop!!
        tabDesktop.newFontMap()
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param fontMapChangeEvent a ChangeEvent object
     */
    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent?) {
        val fontMap = fontMapChangeEvent!!.fontMap
        isEnabled = if (fontMap == null) {
            true
        } else {
            // if fontmap is new, enable new only when the new fontmap had been changed
            // logic is, no need to show 'New' when already a new fontmap open
            if (fontMap.isNew) {
                fontMap.isDirty
            } else {
                true
            }
        }

        //todo: fix-me! reevaluate this logic
        // New action will always be enabled..
        isEnabled = true
    }

    override fun internalFrameActivated(e: InternalFrameEvent) {}
    override fun internalFrameClosed(e: InternalFrameEvent) {}
    override fun internalFrameClosing(e: InternalFrameEvent) {
        isEnabled = true
    }

    override fun internalFrameDeactivated(e: InternalFrameEvent) {
        isEnabled = true
    }

    override fun internalFrameDeiconified(e: InternalFrameEvent) {}
    override fun internalFrameIconified(e: InternalFrameEvent) {}
    override fun internalFrameOpened(e: InternalFrameEvent) {}
}