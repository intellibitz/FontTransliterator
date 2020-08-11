package sted.ui;

import sted.event.IThreadListener;
import sted.event.ThreadEvent;
import sted.io.FileReaderThread;
import sted.io.Resources;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileViewer
        extends JInternalFrame
        implements IThreadListener {
    private JEditorPane editorPane;
    private File file;
    private FileReaderThread fileReaderThread;

    public FileViewer() {
        super(Resources.EMPTY_STRING, false, false, false, false);
        init();
    }

    public FileViewer(Icon icon) {
        this();
        setFrameIcon(icon);
    }


    public void init() {
        setBorder(BorderFactory.createEtchedBorder());
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        final JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(editorPane);
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(scroller);
        fileReaderThread = new FileReaderThread();
        fileReaderThread.addThreadListener(this);
        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Sets the font for this component.
     *
     * @param font the desired <code>Font</code> for this component
     * @see java.awt.Component#getFont
     * <p/>
     * preferred: true bound: true attribute: visualUpdate true
     * description: The font for the component.
     */
    public void setFont(Font font) {
        super.setFont(
                font);    //To change body of overriden methods use Options | File Templates.
        if (editorPane != null) {
            editorPane.setFont(font);
        }
    }

    public void setFileName(String fileName) {
        setTitle(fileName);
        file = new File(fileName);
    }

    public void addThreadListener(IThreadListener threadListener) {
        fileReaderThread.addThreadListener(threadListener);
    }

    public void readFile() {
        fileReaderThread.setFile(file);
        SwingUtilities.invokeLater(fileReaderThread);
    }

    public void threadRunStarted(ThreadEvent e) {
    }

    public void threadRunning(ThreadEvent e) {
    }

    public void threadRunFailed(ThreadEvent e) {
    }

    public void threadRunFinished(ThreadEvent e) {
        editorPane.setText(e.getEventSource().getResult().toString());
    }

}
