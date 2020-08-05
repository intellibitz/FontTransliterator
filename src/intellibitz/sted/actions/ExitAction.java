package intellibitz.sted.actions;

import intellibitz.sted.io.SettingsXMLWriter;
import intellibitz.sted.ui.STEDWindow;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class ExitAction
        extends STEDWindowAction {
    public ExitAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        exit();
    }

    private void exit() {
        STEDWindow stedWindow = getSTEDWindow();
        if (JOptionPane.CANCEL_OPTION !=
                stedWindow.getDesktop().saveDirty()) {
            try {
                SettingsXMLWriter.writeUserSettings(stedWindow);
            } catch (TransformerException ex) {
                logger.severe(
                        "Unable to write User Settings - TransformerException " +
                                ex.getMessage());
                logger.throwing("ExitAction", "exit", ex);
            }
            Runtime.getRuntime().gc();
            System.runFinalization();
            System.exit(0);
        }
    }

    public void windowClosing(WindowEvent e) {
        exit();
    }
}
