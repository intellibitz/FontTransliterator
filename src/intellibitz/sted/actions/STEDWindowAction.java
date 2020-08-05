package intellibitz.sted.actions;

import intellibitz.sted.event.*;
import intellibitz.sted.launch.STEDGUI;
import intellibitz.sted.ui.STEDWindow;

import javax.swing.*;
import java.awt.event.*;
import java.util.logging.Logger;

public class STEDWindowAction
        extends AbstractAction
        implements WindowListener,
        KeyListener,
        WindowStateListener,
        WindowFocusListener,
        IStatusEventSource,
        IMessageEventSource {

    private STEDWindow stedWindow;
    protected Logger logger;
    private StatusEvent statusEvent;
    private IStatusListener statusListener;
    private MessageEvent messageEvent;
    private IMessageListener messageListener;

    public STEDWindowAction() {
        super();
        logger = Logger.getLogger(getClass().getName());
        statusEvent = new StatusEvent(this);
        messageEvent = new MessageEvent(this);
    }

    public void fireMessagePosted(String message) {
        messageEvent.setMessage(message);
        messageListener.messagePosted(messageEvent);
    }

    public void fireMessagePosted() {
        messageListener.messagePosted(messageEvent);
    }

    public void addMessageListener(IMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void fireStatusPosted(String message) {
        statusEvent.setStatus(message);
        statusListener.statusPosted(statusEvent);
    }

    public void fireStatusPosted() {
        statusListener.statusPosted(statusEvent);
    }

    public void addStatusListener(IStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public void actionPerformed(ActionEvent e) {

    }

    public STEDWindow getSTEDWindow() {
        if (null == stedWindow) {
            stedWindow = STEDGUI.getSTEDWindow();
        }
        return stedWindow;
    }

    public void setSTEDWindow(STEDWindow stedWindow) {
        this.stedWindow = stedWindow;
    }

    protected void showMessageDialog(String message) {
        fireMessagePosted(message);
    }

    /**
     * Invoked when a window has been opened.
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Invoked when a window is in the process of being closed. The close
     * operation can be overridden at this point.
     */
    public void windowClosing(WindowEvent e) {
    }

    /**
     * Invoked when a window has been closed.
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when a window is iconified.
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is de-iconified.
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is activated.
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is de-activated.
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Invoked when a window state is changed.
     *
     * @since 1.4
     */
    public void windowStateChanged(WindowEvent e) {
    }

    /**
     * Invoked when the Window is set to be the focused Window, which means that
     * the Window, or one of its subcomponents, will receive keyboard events.
     *
     * @since 1.4
     */
    public void windowGainedFocus(WindowEvent e) {
    }

    /**
     * Invoked when the Window is no longer the focused Window, which means that
     * keyboard events will no longer be delivered to the Window or any of its
     * subcomponents.
     *
     * @since 1.4
     */
    public void windowLostFocus(WindowEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyReleased(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
