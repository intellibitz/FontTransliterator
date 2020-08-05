package intellibitz.sted.actions;

import intellibitz.sted.event.FontMapChangeEvent;
import intellibitz.sted.event.FontMapChangeListener;
import intellibitz.sted.launch.STEDGUI;
import intellibitz.sted.ui.STEDWindow;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;

public class LoadFontMapAction
        extends TableModelListenerAction
        implements FontMapChangeListener,
        InternalFrameListener {
    public LoadFontMapAction() {
        super();
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        setEnabled(
                getSTEDWindow().getDesktop()
                        .getFontMap().isReloadable());
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged(FontMapChangeEvent e) {
        setEnabled(e.getFontMap().isReloadable());
    }

    public void actionPerformed(ActionEvent e) {
        final STEDWindow stedWindow = getSTEDWindow();
        STEDGUI.busy();
        stedWindow.getDesktop().reloadFontMap();
        fireStatusPosted("FontMap Re-Loaded");
        STEDGUI.relax();
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
