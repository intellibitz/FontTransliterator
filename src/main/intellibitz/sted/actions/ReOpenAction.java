package sted.actions;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.fontmap.FontMap;
import sted.ui.STEDWindow;
import sted.util.MenuHandler;
import sted.util.Resources;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.event.ActionEvent;

/**
 * Listens to STEDWindow for any FontMap change event. Listens to
 * FontMapperDesktop for InternalFrame events.
 */
public class ReOpenAction
        extends ReOpenFontMapAction
        implements FontMapChangeListener,
        InternalFrameListener {
    public ReOpenAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void stateChanged(FontMapChangeEvent e) {
        final FontMap fontMap = e.getFontMap();
        if (fontMap.isNew()) {
            MenuHandler.enableReOpenItems(MenuHandler.getInstance());
        } else {
            final String fileName = fontMap.getFileName();
            final STEDWindow stedWindow = getSTEDWindow();
            stedWindow.getDesktop()
                    .addItemToReOpenMenu(fileName);
            // needed when opening a new fontmap
            MenuHandler.disableMenuItem(MenuHandler.getInstance(), fileName);
        }
    }

    /**
     * Invoked when an internal frame is activated.
     *
     * @see javax.swing.JInternalFrame#setSelected
     */
    public void internalFrameActivated(InternalFrameEvent e) {
        MenuHandler.enableItemsInReOpenMenu(MenuHandler.getInstance(),
                getSTEDWindow().getDesktop()
                        .getFontMap());
    }

    /**
     * Invoked when a internal frame has been opened.
     *
     * @see javax.swing.JInternalFrame#show
     */
    public void internalFrameOpened(InternalFrameEvent e) {
        MenuHandler.enableItemsInReOpenMenu(MenuHandler.getInstance(),
                getSTEDWindow()
                        .getDesktop()
                        .getFontMap());
    }


    /**
     * Invoked when an internal frame is de-activated.
     *
     * @see javax.swing.JInternalFrame#setSelected
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {
        addItemToReOpenMenu();
    }

    /**
     * Invoked when an internal frame is in the process of being closed. The
     * close operation can be overridden at this point.
     *
     * @see javax.swing.JInternalFrame#setDefaultCloseOperation
     */
    public void internalFrameClosing(InternalFrameEvent e) {
        addItemToReOpenMenu();
    }

    /**
     * Invoked when an internal frame has been closed.
     *
     * @see javax.swing.JInternalFrame#setClosed
     */
    public void internalFrameClosed(InternalFrameEvent e) {
    }

    /**
     * Invoked when an internal frame is de-iconified.
     *
     * @see javax.swing.JInternalFrame#setIcon
     */
    public void internalFrameDeiconified(InternalFrameEvent e) {

    }

    /**
     * Invoked when an internal frame is iconified.
     *
     * @see javax.swing.JInternalFrame#setIcon
     */
    public void internalFrameIconified(InternalFrameEvent e) {

    }

    public void addItemToReOpenMenu() {
        final STEDWindow stedWindow = getSTEDWindow();
        final MenuHandler menuHandler = MenuHandler.getInstance();
        final FontMap fontMap = stedWindow.getDesktop()
                .getFontMap();
        final JMenu menu =
                menuHandler.getMenu(Resources.ACTION_FILE_REOPEN_COMMAND);
        if (!fontMap.isNew()) {
            MenuHandler.addReOpenItem(menu, fontMap.getFileName());
        }
        MenuHandler.enableReOpenItems(menu);
    }


}
