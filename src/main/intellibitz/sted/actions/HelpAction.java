package sted.actions;

import sted.ui.HelpWindow;

import java.awt.event.ActionEvent;

public class HelpAction
        extends STEDWindowAction {
    public HelpAction() {
    }

    public void actionPerformed(ActionEvent e) {
        HelpWindow.getInstance().setVisible(true);
    }

}
