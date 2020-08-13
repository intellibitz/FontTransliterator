package sted.actions

import sted.STEDGUI
import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import java.awt.event.ActionEvent
import javax.swing.event.InternalFrameEvent
import javax.swing.event.InternalFrameListener
import javax.swing.event.TableModelEvent

class LoadFontMapAction : TableModelListenerAction(), FontMapChangeListener, InternalFrameListener {
    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        isEnabled = stedWindow.desktop.fontMap.isReloadable
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param fontMapChangeEvent a ChangeEvent object
     */
    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        isEnabled = fontMapChangeEvent.fontMap.isReloadable
    }

    override fun actionPerformed(e: ActionEvent) {
        STEDGUI.busy()
        stedWindow.desktop.reloadFontMap()
        fireStatusPosted("FontMap Re-Loaded")
        STEDGUI.relax()
    }

    override fun internalFrameActivated(e: InternalFrameEvent) {}
    override fun internalFrameClosed(e: InternalFrameEvent) {}
    override fun internalFrameClosing(e: InternalFrameEvent) {
        isEnabled = false
    }

    override fun internalFrameDeactivated(e: InternalFrameEvent) {}
    override fun internalFrameDeiconified(e: InternalFrameEvent) {}
    override fun internalFrameIconified(e: InternalFrameEvent) {}
    override fun internalFrameOpened(e: InternalFrameEvent) {}
}