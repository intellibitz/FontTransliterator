package sted.widgets;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentListenerButton
        extends JButton
        implements DocumentListener {
    public DocumentListenerButton() {
    }

    public void insertUpdate(DocumentEvent e) {
        toggle(e);
    }

    public void removeUpdate(DocumentEvent e) {
        toggle(e);
    }

    public void changedUpdate(DocumentEvent e) {
        toggle(e);
    }

    private void toggle(DocumentEvent e) {
        if (e.getDocument().getLength() > 0) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }
}

