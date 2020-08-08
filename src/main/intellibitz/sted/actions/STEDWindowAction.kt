package sted.actions

import sted.STEDGUI
import sted.event.*
import sted.ui.STEDWindow
import java.awt.event.*
import java.util.logging.Logger
import javax.swing.AbstractAction

open class STEDWindowAction : AbstractAction(), WindowListener, KeyListener, WindowStateListener, WindowFocusListener,
    IStatusEventSource, IMessageEventSource {
    private val statusEvent: StatusEvent
    private val messageEvent: MessageEvent
    //    private var stedWindow: STEDWindow? = null
    val stedWindow: STEDWindow by lazy {
        STEDGUI.sTEDWindow
    }

    @JvmField
    protected var logger = Logger.getLogger(javaClass.name)
    private var statusListener: IStatusListener? = null
    private var messageListener: IMessageListener? = null
    fun fireMessagePosted(message: String?) {
        messageEvent.message = message
        messageListener!!.messagePosted(messageEvent)
    }

    override fun fireMessagePosted() {
        messageListener!!.messagePosted(messageEvent)
    }

    override fun addMessageListener(messageListener: IMessageListener) {
        this.messageListener = messageListener
    }

    fun fireStatusPosted(message: String?) {
        statusEvent.status = message
        statusListener!!.statusPosted(statusEvent)
    }

    override fun fireStatusPosted() {
        statusListener!!.statusPosted(statusEvent)
    }

    override fun addStatusListener(statusListener: IStatusListener) {
        this.statusListener = statusListener
    }

    override fun actionPerformed(e: ActionEvent) {}

    /**
     * Invoked when a window has been opened.
     */
    override fun windowOpened(e: WindowEvent) {}

    /**
     * Invoked when a window is in the process of being closed. The close
     * operation can be overridden at this point.
     */
    override fun windowClosing(e: WindowEvent) {}

    /**
     * Invoked when a window has been closed.
     */
    override fun windowClosed(e: WindowEvent) {}

    /**
     * Invoked when a window is iconified.
     */
    override fun windowIconified(e: WindowEvent) {}

    /**
     * Invoked when a window is de-iconified.
     */
    override fun windowDeiconified(e: WindowEvent) {}

    /**
     * Invoked when a window is activated.
     */
    override fun windowActivated(e: WindowEvent) {}

    /**
     * Invoked when a window is de-activated.
     */
    override fun windowDeactivated(e: WindowEvent) {}

    /**
     * Invoked when a window state is changed.
     *
     * @since 1.4
     */
    override fun windowStateChanged(e: WindowEvent) {}

    /**
     * Invoked when the Window is set to be the focused Window, which means that
     * the Window, or one of its subcomponents, will receive keyboard events.
     *
     * @since 1.4
     */
    override fun windowGainedFocus(e: WindowEvent) {}

    /**
     * Invoked when the Window is no longer the focused Window, which means that
     * keyboard events will no longer be delivered to the Window or any of its
     * subcomponents.
     *
     * @since 1.4
     */
    override fun windowLostFocus(e: WindowEvent) {}
    override fun keyTyped(e: KeyEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    override fun keyPressed(e: KeyEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    override fun keyReleased(e: KeyEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    init {
        statusEvent = StatusEvent(this)
        messageEvent = MessageEvent(this)
    }
}