package sted.actions

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.ui.MenuHandler
import java.awt.event.ActionEvent
import javax.swing.event.InternalFrameEvent
import javax.swing.event.InternalFrameListener

/**
 * Listens to STEDWindow for any FontMap change event. Listens to
 * FontMapperDesktop for InternalFrame events.
 */
class ReOpenAction : ReOpenFontMapAction(), FontMapChangeListener, InternalFrameListener {
    override fun actionPerformed(e: ActionEvent) {}
    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        val fontMap = fontMapChangeEvent.fontMap
        if (fontMap.isNew) {
            MenuHandler.enableReOpenItems()
        } else {
            val fileName = fontMap.fileName
            stedWindow.desktop
                .addItemToReOpenMenu(fileName)
            // needed when opening a new fontmap
            MenuHandler.disableMenuItem(fileName)
        }
    }

    /**
     * Invoked when an internal frame is activated.
     *
     * @see javax.swing.JInternalFrame.setSelected
     */
    override fun internalFrameActivated(e: InternalFrameEvent) {
        MenuHandler.enableItemsInReOpenMenu(
            stedWindow.desktop
                .fontMap
        )
    }

    /**
     * Invoked when a internal frame has been opened.
     *
     * @see javax.swing.JInternalFrame.show
     */
    override fun internalFrameOpened(e: InternalFrameEvent) {
        MenuHandler.enableItemsInReOpenMenu(
            stedWindow
                .desktop
                .fontMap
        )
    }

    /**
     * Invoked when an internal frame is de-activated.
     *
     * @see javax.swing.JInternalFrame.setSelected
     */
    override fun internalFrameDeactivated(e: InternalFrameEvent) {
        addItemToReOpenMenu()
    }

    /**
     * Invoked when an internal frame is in the process of being closed. The
     * close operation can be overridden at this point.
     *
     * @see javax.swing.JInternalFrame.setDefaultCloseOperation
     */
    override fun internalFrameClosing(e: InternalFrameEvent) {
        addItemToReOpenMenu()
    }

    /**
     * Invoked when an internal frame has been closed.
     *
     * @see javax.swing.JInternalFrame.setClosed
     */
    override fun internalFrameClosed(e: InternalFrameEvent) {}

    /**
     * Invoked when an internal frame is de-iconified.
     *
     * @see javax.swing.JInternalFrame.setIcon
     */
    override fun internalFrameDeiconified(e: InternalFrameEvent) {}

    /**
     * Invoked when an internal frame is iconified.
     *
     * @see javax.swing.JInternalFrame.setIcon
     */
    override fun internalFrameIconified(e: InternalFrameEvent) {}
    fun addItemToReOpenMenu() {
        val fontMap = stedWindow.desktop
            .fontMap
        val menu = MenuHandler.menus["ReOpen"]!!
        if (!fontMap.isNew) {
            MenuHandler.addReOpenItem(menu, fontMap.fileName)
        }
        MenuHandler.enableReOpenItems(menu)
    }
}