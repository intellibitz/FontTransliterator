package intellibitz.sted.actions;

import intellibitz.sted.ui.AboutSTED;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AboutAction
        extends AbstractAction {
    public AboutAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        AboutSTED.getInstance().setVisible(true);
    }

}
