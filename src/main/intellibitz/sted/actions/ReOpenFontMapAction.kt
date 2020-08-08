package sted.actions;

import sted.STEDGUI;
import sted.ui.STEDWindow;
import sted.ui.TabDesktop;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ReOpenFontMapAction
        extends OpenFontMapAction {
    public ReOpenFontMapAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        final STEDWindow stedWindow = getSTEDWindow();
        STEDGUI.busy();
        final TabDesktop tabDesktop =
                stedWindow.getDesktop();
//        if (JOptionPane.CANCEL_OPTION != tabDesktop.saveDirty())
//        {
        final String fileName = (String) getValue(Action.NAME);
        tabDesktop.reopenFontMap(fileName);
//        }
        STEDGUI.relax();
    }

}
