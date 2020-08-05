package intellibitz.sted.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GCButton
        extends JButton
        implements ActionListener {
    private final Runtime runtime;

    public GCButton(Icon icon, Icon rollOverIcon) {
        super();
        setIcon(icon);
        setRolloverIcon(rollOverIcon);
        setRolloverEnabled(true);
        setMargin(new Insets(0, 0, 0, 0));
        setSize(new Dimension(icon.getIconHeight(), icon.getIconWidth()));
        setToolTipText("Runs Garbage Collector");
        runtime = Runtime.getRuntime();
        // add the button as the self action listener
        // the button knows what to do anyways
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        runtime.runFinalization();
        runtime.gc();
    }

}
