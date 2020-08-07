package sted.ui;

import sted.io.Resources;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

/**
 * sets HTMLEditorKit. reads Resources for the text to be displayed. Singleton.
 */
public class AboutText
        extends JTextPane {

    private static AboutText aboutText;

    private AboutText() {
        super();
        setEditable(false);
        setSize(400, 400);
        setEditorKit(new HTMLEditorKit());
        setText(Resources.getResource("about.dialog.text"));
    }

    public static synchronized AboutText getInstance() {
        if (aboutText == null) {
            aboutText = new AboutText();
        }
        return aboutText;
    }
}
