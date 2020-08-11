package sted.ui;

import org.jetbrains.annotations.NotNull;
import sted.event.IMessageEventSource;
import sted.event.IMessageListener;
import sted.event.MessageEvent;
import sted.io.FileHelper;
import sted.io.Resources;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import java.util.logging.Logger;

public class HelpWindow
        extends JFrame
        implements HyperlinkListener,
        IMessageEventSource {
    private final JTextPane textPane;
    private final Stack<URL> currentPages;
    private final Stack<URL> backPages;
    private final Stack<URL> forwardPages;
    private final JButton backButton;
    private final JButton forwardButton;
    private final JButton homeButton;
    private URL homepage;

    private static final String HELP_INDEX = FileHelper.suffixFileSeparator
            (System.getProperty(Resources.STED_HOME_PATH, "./")) + Resources
            .getResource(Resources.HELP_INDEX);
    private static final Logger logger =
            Logger.getLogger(HelpWindow.class.getName());

    private static HelpWindow helpWindow;
    private MessageEvent messageEvent;
    private IMessageListener messageListener;

    public static synchronized HelpWindow getInstance() {
        if (helpWindow == null) {
            helpWindow = new HelpWindow();
            helpWindow.setSize(600, 800);
        }
        return helpWindow;
    }


    private HelpWindow() {
        super(Resources.getResource(Resources.TITLE_HELP));
        setIconImage(Resources.getSystemResourceIcon(
                Resources.getResource(Resources.ICON_HELP)).getImage());
        currentPages = new Stack<URL>();
        backPages = new Stack<URL>();
        forwardPages = new Stack<URL>();
        final JToolBar jToolBar = new JToolBar(JToolBar.HORIZONTAL);
        jToolBar.setFloatable(false);
        homeButton = new JButton(Resources.getSystemResourceIcon(
                Resources.getResource(Resources.ICON_HELP_HOME)));
        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goHome();
            }
        });
        homeButton.setToolTipText("Table Of Contents");
        //
        jToolBar.add(homeButton);
        backButton = new JButton(Resources.getSystemResourceIcon(
                Resources.getResource(Resources.ICON_HELP_BACK)));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        backButton.setToolTipText("Back");
        //
        jToolBar.add(backButton);
        forwardButton = new JButton(
                Resources.getSystemResourceIcon(
                        Resources.getResource(Resources.ICON_HELP_FORWARD)));
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goForward();
            }
        });
        forwardButton.setToolTipText("Forward");
        //
        jToolBar.add(forwardButton);
        // add the toolbar
        // NOTE: "North" is required.. else the toolbar is not visible.
        getContentPane().add("North", jToolBar);
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setSize(400, 400);
        textPane.setEditorKit(new HTMLEditorKit());
        textPane.addHyperlinkListener(this);
        final JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(textPane);
        getContentPane().add(scroller);
        goHome();
        setSize(textPane.getSize());
        setDefaultLookAndFeelDecorated(true);
        setState(JFrame.MAXIMIZED_HORIZ);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });

        messageEvent = new MessageEvent(this);
        pack();
    }

    public void fireMessagePosted(String message) {
        messageEvent.setMessage(message);
        messageListener.messagePosted(messageEvent);
    }

    public void fireMessagePosted() {
        messageListener.messagePosted(messageEvent);
    }

    public void addMessageListener(@NotNull IMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    private void goHome() {
        // if first time, then should be the startup
        if (homepage == null) {
            go(HELP_INDEX);
            homepage = textPane.getPage();
        } else {
            setURL(homepage);
        }
    }

    private void goBack() {
        final URL url = backPages.pop();
        // push the current page into the forward stack
        if (currentPages.isEmpty()) {
            forwardPages.push(url);
        } else {
            forwardPages.push(currentPages.pop());
        }
        setPage(url);
    }

    private void goForward() {
        final URL url = forwardPages.pop();
        // push the current page into the forward stack
        if (currentPages.isEmpty()) {
            backPages.push(url);
        } else {
            backPages.push(currentPages.pop());
        }
        setPage(url);
    }

    private void setPage(URL url) {
        try {
            textPane.setPage(url);
            // push the current page
            currentPages.push(url);
            setButtonState();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            fireMessagePosted("Cannot set page " + e.getMessage());
        }
    }

    private void setURL(URL url) {
        try {
            textPane.setPage(url);
            // pop the previous page and push it into back
            if (!currentPages.isEmpty()) {
                backPages.push(currentPages.pop());
            }
            // clear the forward currentPages
            forwardPages.clear();
            // push the current page
            currentPages.push(url);
        } catch (IOException e) {
            logger.severe("Cannot set page " + url);
            fireMessagePosted("Cannot set page " + url);
        }
        setButtonState();
    }

    private void setButtonState() {
        backButton.setEnabled(!backPages.isEmpty());
        forwardButton.setEnabled(!forwardPages.isEmpty());
        final URL index = textPane.getPage();
        homeButton.setEnabled(
                index != null && index.getPath().indexOf(HELP_INDEX) == -1);
    }


    private void go(String path) {
        try {
            if (path != null) {
                // check if the path is relative or absolute
                // if relative, then get the absolute path.. this happens the first time showing Help
                if (path.indexOf(":") == -1) {
                    final File file = new File(path);
                    setURL(new URL("file:///" + file.getAbsolutePath()));
                } else {
                    setURL(new URL("file:///" + path));
                }
            }
        } catch (IOException e) {
            logger.throwing(getClass().getName(), "actionPerformed", e);
            fireMessagePosted("Cannot go to page - IOException occured: " +
                    e.getMessage());
        }
    }

    private void go(URL url) {
        if (url.getProtocol().startsWith("file")) {
            go(url.getPath());
        } else if (url.getProtocol().startsWith("http")) {
            // TODO: NEED TO IMPLEMENT
        } else {
            setURL(url);
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            go(e.getURL());
        }
    }

}
