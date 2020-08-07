package sted.actions;

import sted.fontmap.FontMap;
import sted.ui.STEDWindow;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.event.ActionEvent;

public class SaveAsAction
        extends TableModelListenerAction
        implements InternalFrameListener {
    public SaveAsAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        final STEDWindow stedWindow = getSTEDWindow();
        if (JFileChooser.ERROR_OPTION ==
                stedWindow.getDesktop().saveAsAction()) {
            fireMessagePosted("Cannot Save.. Error" +
                    JFileChooser.ERROR_OPTION);
            return;
        }
        final FontMap fontMap = stedWindow.getDesktop()
                .getFontMap();
        fontMap.fireUndoEvent();
        fontMap.fireRedoEvent();
        fireStatusPosted("Saved");
    }

    public void internalFrameActivated(InternalFrameEvent e) {

    }

    public void internalFrameClosed(InternalFrameEvent e) {

    }

    public void internalFrameClosing(InternalFrameEvent e) {
        setEnabled(false);
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {

    }

    public void internalFrameDeiconified(InternalFrameEvent e) {

    }

    public void internalFrameIconified(InternalFrameEvent e) {

    }

    public void internalFrameOpened(InternalFrameEvent e) {

    }

}
