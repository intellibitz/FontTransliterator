package sted.actions;

import sted.STEDGUI;
import sted.ui.STEDWindow;
import sted.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileSelectAction
        extends TableModelListenerAction {
    public FileSelectAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        final String cmd = e.getActionCommand();
        boolean isInput = true;
        if (Resources.ACTION_SELECT_INPUT_FILE_COMMAND.equalsIgnoreCase(cmd)) {
            isInput = true;
        } else if (Resources.ACTION_SELECT_OUTPUT_FILE_COMMAND.equalsIgnoreCase(cmd)) {
            isInput = false;
        }
        File file;
        final STEDWindow stedWindow = getSTEDWindow();
        if (isInput) {
            file = stedWindow.getDesktop()
                    .getDesktopModel()
                    .getInputFile();
        } else {
            file = stedWindow.getDesktop()
                    .getDesktopModel()
                    .getOutputFile();
        }
        final JFileChooser jFileChooser;
        final int result;
        if (file == null) {
            jFileChooser = new JFileChooser(System.getProperty("user.dir"));
            result = jFileChooser.showOpenDialog(stedWindow);
        } else {
            jFileChooser = new JFileChooser(file.getParent());
            result = jFileChooser.showSaveDialog(stedWindow);
        }
        if (result == JFileChooser.APPROVE_OPTION) {
            file = jFileChooser.getSelectedFile();
            if (file != null) {
                STEDGUI.busy();
                if (isInput) {
                    stedWindow.getDesktop()
                            .getFontMapperDesktopFrame().setInputFile(file);
                } else {
                    stedWindow.getDesktop()
                            .getFontMapperDesktopFrame().setOutputFile(file);
                }
                stedWindow.getDesktop()
                        .getFontMapperDesktopFrame()
                        .enableConverterIfFilesLoaded();
                STEDGUI.relax();
            }
        }
    }

}
