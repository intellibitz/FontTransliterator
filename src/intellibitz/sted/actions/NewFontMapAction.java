package intellibitz.sted.actions;

import intellibitz.sted.event.FontMapChangeEvent;
import intellibitz.sted.event.FontMapChangeListener;
import intellibitz.sted.fontmap.FontMap;
import intellibitz.sted.ui.TabDesktop;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.event.ActionEvent;

public class NewFontMapAction
        extends STEDWindowAction
        implements FontMapChangeListener,
        InternalFrameListener {
    public NewFontMapAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        final TabDesktop tabDesktop =
                getSTEDWindow().getDesktop();
        tabDesktop.newFontMap();
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param fontMapChangeEvent a ChangeEvent object
     */
    public void stateChanged(FontMapChangeEvent fontMapChangeEvent) {
        final FontMap fontMap = fontMapChangeEvent.getFontMap();
        if (fontMap == null) {
            setEnabled(true);
        } else {
            // if fontmap is new, enable new only when the new fontmap had been changed
            // logic is, no need to show 'New' when already a new fontmap open
            if (fontMap.isNew()) {
                setEnabled(fontMap.isDirty());
            } else {
                setEnabled(true);
            }
        }

        //todo: fix-me! reevaluate this logic
        // New action will always be enabled..
        setEnabled(true);
    }

    public void internalFrameActivated(InternalFrameEvent e) {
    }

    public void internalFrameClosed(InternalFrameEvent e) {

    }

    public void internalFrameClosing(InternalFrameEvent e) {
        setEnabled(true);
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
        setEnabled(true);
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {

    }

    public void internalFrameIconified(InternalFrameEvent e) {

    }

    public void internalFrameOpened(InternalFrameEvent e) {
    }


}
