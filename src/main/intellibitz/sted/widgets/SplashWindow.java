package intellibitz.sted.widgets;

import intellibitz.sted.event.IStatusListener;
import intellibitz.sted.event.StatusEvent;

import javax.swing.*;
import java.awt.*;

public class SplashWindow
        extends JWindow
        implements IStatusListener {
    private final JProgressBar progress;

    public SplashWindow(Component component) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(component);
        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        contentPane.add(progress);
        pack();
    }

    public void setProgress(int percent) {
        progress.setValue(percent);
    }

    public void statusPosted(StatusEvent event) {
        setProgress(Integer.valueOf(event.getStatus()));
    }
}
