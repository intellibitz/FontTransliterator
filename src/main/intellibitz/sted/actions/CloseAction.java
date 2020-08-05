package intellibitz.sted.actions;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.event.ActionEvent;

public class CloseAction
        extends STEDWindowAction
        implements InternalFrameListener {
    public CloseAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        getSTEDWindow().getDesktop().closeFontMap();
        fireStatusPosted("FontMap closed");
    }

    public void internalFrameActivated(InternalFrameEvent e) {
        setEnabled(true);
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
