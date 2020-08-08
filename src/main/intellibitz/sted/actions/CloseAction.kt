package sted.actions

import java.awt.event.ActionEvent
import javax.swing.event.InternalFrameEvent
import javax.swing.event.InternalFrameListener

class CloseAction : STEDWindowAction(), InternalFrameListener {
    override fun actionPerformed(e: ActionEvent) {
        sTEDWindow!!.desktop.closeFontMap()
        fireStatusPosted("FontMap closed")
    }

    override fun internalFrameActivated(e: InternalFrameEvent) {
        isEnabled = true
    }

    override fun internalFrameClosed(e: InternalFrameEvent) {}
    override fun internalFrameClosing(e: InternalFrameEvent) {
        isEnabled = false
    }

    override fun internalFrameDeactivated(e: InternalFrameEvent) {}
    override fun internalFrameDeiconified(e: InternalFrameEvent) {}
    override fun internalFrameIconified(e: InternalFrameEvent) {}
    override fun internalFrameOpened(e: InternalFrameEvent) {}
}