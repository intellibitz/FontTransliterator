package sted.ui

import sted.event.IMessageEventSource
import sted.event.IMessageListener
import sted.event.MessageEvent
import sted.io.Resources.getResource
import sted.io.Resources.getResourceIcon
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.logging.Logger
import javax.swing.*
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import javax.swing.text.html.HTMLEditorKit

class HelpWindow private constructor() : JFrame(), HyperlinkListener, IMessageEventSource {
    private val textPane = JTextPane()
    private val currentPages = Stack<URL>()
    private val backPages = Stack<URL>()
    private val forwardPages = Stack<URL>()
    private val backButton = JButton()
    private val forwardButton = JButton()
    private val homeButton = JButton()
    private val messageEvent = MessageEvent(this)
    private var homepage: URL? = null
    private var messageListener: IMessageListener? = null
    private fun load() {
        title = getResource("title.help")
        val imageIcon = getResourceIcon(
            "icon.help"
        )
        if (imageIcon != null) iconImage = imageIcon.image
        val jToolBar = JToolBar(JToolBar.HORIZONTAL)
        jToolBar.isFloatable = false
        homeButton.icon = getResourceIcon(
            "icon.help.home"
        )
        homeButton.addActionListener { goHome() }
        homeButton.toolTipText = "Table Of Contents"
        //
        jToolBar.add(homeButton)
        backButton.icon = getResourceIcon(
            "icon.help.back"
        )
        backButton.addActionListener { goBack() }
        backButton.toolTipText = "Back"
        //
        jToolBar.add(backButton)
        forwardButton.icon = getResourceIcon(
            "icon.help.forward"
        )
        forwardButton.addActionListener { goForward() }
        forwardButton.toolTipText = "Forward"
        //
        jToolBar.add(forwardButton)
        // add the toolbar
        // NOTE: "North" is required.. else the toolbar is not visible.
        contentPane.add("North", jToolBar)
        textPane.isEditable = false
        textPane.setSize(400, 400)
        textPane.editorKit = HTMLEditorKit()
        textPane.addHyperlinkListener(this)
        val scroller = JScrollPane()
        scroller.viewport.add(textPane)
        contentPane.add(scroller)
        val resource = getResource("help.index")
        if (!resource.isNullOrEmpty())
            HELP_INDEX = resource
        goHome()
        size = textPane.size
        setDefaultLookAndFeelDecorated(true)
        state = MAXIMIZED_HORIZ
        extendedState = MAXIMIZED_BOTH
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                isVisible = false
            }
        })

//        messageEvent = new MessageEvent(this);
        pack()
    }

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

    private fun goHome() {
        // if first time, then should be the startup
        if (homepage == null) {
            go(HELP_INDEX)
            homepage = textPane.page
        } else {
            setURL(homepage!!)
        }
    }

    private fun goBack() {
        val url = backPages.pop()
        // push the current page into the forward stack
        if (currentPages.isEmpty()) {
            forwardPages.push(url)
        } else {
            forwardPages.push(currentPages.pop())
        }
        setPage(url)
    }

    private fun goForward() {
        val url = forwardPages.pop()
        // push the current page into the forward stack
        if (currentPages.isEmpty()) {
            backPages.push(url)
        } else {
            backPages.push(currentPages.pop())
        }
        setPage(url)
    }

    private fun setPage(url: URL) {
        try {
            textPane.page = url
            // push the current page
            currentPages.push(url)
            setButtonState()
        } catch (e: IOException) {
            logger.severe(e.message)
            fireMessagePosted("Cannot set page " + e.message)
        }
    }

    private fun setURL(url: URL) {
        try {
            textPane.page = url
            // pop the previous page and push it into back
            if (!currentPages.isEmpty()) {
                backPages.push(currentPages.pop())
            }
            // clear the forward currentPages
            forwardPages.clear()
            // push the current page
            currentPages.push(url)
        } catch (e: IOException) {
            logger.severe("Cannot set page $url")
            fireMessagePosted("Cannot set page $url")
        }
        setButtonState()
    }

    private fun setButtonState() {
        backButton.isEnabled = !backPages.isEmpty()
        forwardButton.isEnabled = !forwardPages.isEmpty()
        val index = textPane.page
        homeButton.isEnabled = index != null && !index.path.contains(HELP_INDEX)
    }

    private fun go(path: String?) {
        try {
            if (path != null) {
                // check if the path is relative or absolute
                // if relative, then get the absolute path.. this happens the first time showing Help
                if (!path.contains(":")) {
                    val file = File(path)
                    setURL(URL("file:///" + file.absolutePath))
                } else {
                    setURL(URL("file:///$path"))
                }
            }
        } catch (e: IOException) {
            logger.throwing(javaClass.name, "actionPerformed", e)
            fireMessagePosted(
                "Cannot go to page - IOException occured: " +
                        e.message
            )
        }
    }

    private fun go(url: URL) {
        if (url.protocol.startsWith("file")) {
            go(url.path)
        } else {
            setURL(url)
        }
    }

    override fun hyperlinkUpdate(e: HyperlinkEvent) {
        if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
            go(e.url)
        }
    }

    companion object {
        private val logger = Logger.getLogger(HelpWindow::class.java.name)
        private var HELP_INDEX: String = "index.html"
        private lateinit var helpWindow: HelpWindow

        @get:Synchronized
        val instance: HelpWindow
            get() {
                helpWindow = HelpWindow()
                helpWindow.load()
                helpWindow.setSize(600, 800)
                return helpWindow
            }
    }
}