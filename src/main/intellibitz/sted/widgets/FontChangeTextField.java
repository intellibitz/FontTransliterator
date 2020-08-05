package intellibitz.sted.widgets;

import intellibitz.sted.event.IKeypadListener;
import intellibitz.sted.event.KeypadEvent;
import intellibitz.sted.ui.FontKeypad;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class FontChangeTextField
        extends JTextField
        implements ItemListener,
        ActionListener,
        TableModelListener,
        IKeypadListener {
    public FontChangeTextField() {
    }

    public void itemStateChanged(ItemEvent e) {
        Font f = new Font(e.getItem().toString(), Font.PLAIN, 14);
        setFont(f);
    }

    public void actionPerformed(ActionEvent e) {
        setText(getText() + ((JButton) e.getSource()).getText());
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        requestFocus();
    }

    public void keypadReset(KeypadEvent event) {
        FontKeypad fontKeypad = (FontKeypad) event.getEventSource();
        List<JButton> keys = fontKeypad.getKeys();
        final int sz = keys.size();
        for (int i = 0; i < sz; i++) {
            (keys.get(i)).addActionListener(this);
        }
    }
}
