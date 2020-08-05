package intellibitz.sted.actions;

import intellibitz.sted.ui.STEDWindow;

import java.awt.event.ActionEvent;

public class OpenFontMapAction
        extends STEDWindowAction {
    public OpenFontMapAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        final STEDWindow stedWindow = getSTEDWindow();
//        STEDGUI.busy();
//        if (JOptionPane.CANCEL_OPTION !=
//                stedWindow.getDesktop().saveDirty())
//        {
        stedWindow.getDesktop().openFontMap();
//        }
//        STEDGUI.relax();
    }

}
