package intellibitz.sted.actions;

import intellibitz.sted.ui.FontKeypad;
import intellibitz.sted.util.FileHelper;
import intellibitz.sted.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LoadFontAction
        extends STEDWindowAction {
    private FontKeypad fontKeypad;

    public LoadFontAction(FontKeypad fontSelectPanel) {
        super();
        putValue(Action.NAME, Resources.getSetting(Resources.LABEL_FONT_LOAD));
        fontKeypad = fontSelectPanel;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        loadFont();
    }

    private void loadFont() {
        final File file = FileHelper.openFont(getSTEDWindow());
        if (file != null) {
            fontKeypad.loadFont(file);
        }
    }


}

