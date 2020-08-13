package sted.ui

import sted.actions.ExitAction
import sted.actions.ItemListenerAction
import sted.actions.STEDWindowAction
import sted.event.*
import sted.io.FileHelper.getSampleFontMapPaths
import sted.io.FileReaderThread
import sted.io.Resources
import sted.io.Resources.getResource
import sted.io.Resources.getSetting
import sted.io.Resources.getSettingBeginsWith
import sted.io.Resources.getSystemResourceIcon
import sted.ui.MenuHandler.Companion.addReOpenItem
import sted.ui.MenuHandler.Companion.addSampleFontMapMenuItem
import sted.ui.MenuHandler.Companion.loadLookAndFeelMenu
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.util.logging.LogManager
import java.util.logging.Logger
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class STEDWindow : JFrame(), IThreadListener, ChangeListener, IMessageListener, IStatusEventSource {
    // desktop to show fontmap
    val desktop: TabDesktop = TabDesktop()
    val statusPanel: StatusPanel = StatusPanel()
    private lateinit var statusEvent: StatusEvent
    private lateinit var statusListener: IStatusListener
    lateinit var logManager: LogManager

    val logger: Logger = Logger.getLogger(STEDWindow::class.java.name)
    fun init() {
        setDefaultLookAndFeelDecorated(true)
        state = MAXIMIZED_BOTH
        extendedState = MAXIMIZED_BOTH
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        title = getResource("title")
        val imageIcon = getSystemResourceIcon(getSetting("icon.sted"))
        if (imageIcon != null) iconImage = imageIcon.image
        statusEvent = StatusEvent(this)
        logManager.addLogger(logger)
        val xml = getResource("config.menu")
        if (xml == null) {
            logger.severe("Load menu file not found: please check config.menu property to set menu file")
        } else {
            MenuHandler.menuHandler.loadMenu(xml)
        }
        val menuBar = MenuHandler.menuHandler.getMenuBar("STED-MenuBar")
        loadLookAndFeelMenu()

        // load the menubar for the application
        jMenuBar = menuBar
        fireStatusPosted("20")
        val container = contentPane
        val gridBagLayout = GridBagLayout()
        container.layout = gridBagLayout
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = 1
        gridBagConstraints.gridheight = 1
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.weighty = 0.0
        gridBagConstraints.gridx = 0
        gridBagConstraints.gridy = 0
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        fireStatusPosted("30")
        val toolBar = MenuHandler.menuHandler.getToolBar(Resources.MENUBAR_STED)
        gridBagLayout.setConstraints(toolBar, gridBagConstraints)

        // adds the toolbar for the app
        container.add(toolBar)
        fireStatusPosted("40")

        desktop.init()
        fireStatusPosted("50")
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.gridy = 1
        gridBagConstraints.fill = GridBagConstraints.BOTH
        // adds the desktop directly
        gridBagLayout.setConstraints(desktop, gridBagConstraints)
        container.add(desktop)
        fireStatusPosted("60")
        statusPanel.load(this)
        gridBagConstraints.weighty = 0.0
        gridBagConstraints.gridy = 2
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagLayout.setConstraints(statusPanel, gridBagConstraints)

        // adds the status bar
        container.add(statusPanel)
        fireStatusPosted("70")
        setUserOptions()
        addMouseListener(AboutSTED.instance)
        val exitAction = ExitAction()
        //        exitAction.setSTEDWindow(STEDWindow.this);
        addWindowListener(exitAction)
        pack()
        logger.finest("successfully intialized STEDWindow")
        fireStatusPosted("80")
    }

    fun load() {
        // status panel added as status listener to recieve status messages
        desktop.addStatusListener(statusPanel)
        val actions = MenuHandler.menuHandler.actions
        for (action in actions.values) {
            if (action is STEDWindowAction) {
                action.addStatusListener(statusPanel)
                action.addMessageListener(this)
            }
        }
        desktop.load()
        desktop.addChangeListener(this)
        state = MAXIMIZED_HORIZ
        extendedState = MAXIMIZED_BOTH
    }

    fun fireStatusPosted(message: String?) {
        statusEvent.status = message
        statusListener.statusPosted(statusEvent)
    }

    override fun fireStatusPosted() {
        statusListener.statusPosted(statusEvent)
    }

    override fun addStatusListener(statusListener: IStatusListener) {
        this.statusListener = statusListener
    }

    fun setVisible() {
        super.setVisible(true)
        statusPanel.runMemoryBar()
    }

    private fun setUserOptions() {
        val menuItems = MenuHandler.menuHandler.menuItems
        for (key in menuItems.keys) {
            val menuItem = menuItems[key]
            val action = menuItem!!.action
            if (action is ItemListenerAction) {
                val `val` = getSetting(key)
                if (`val` != null) {
                    val curr = `val`.toBoolean()
                    // first check for state for disabled menu items
                    if (!action.isEnabled()) {
                        menuItem.isSelected = curr
                    } else if (curr && !menuItem.isSelected) {
                        // if user option is set, and the menu item is already not selected
                        menuItem.doClick()
                    } else if (menuItem.isSelected && !curr) {
                        menuItem.doClick()
                    }
                }
            }
        }
        val reopenItems = getSettingBeginsWith(Resources.ACTION_FILE_REOPEN_COMMAND)
        if (reopenItems.isNotEmpty()) {
            val menu = MenuHandler.menuHandler.getMenu(Resources.ACTION_FILE_REOPEN_COMMAND)
            for (reopenItem in reopenItems) {
                addReOpenItem(menu!!, reopenItem)
            }
            menu!!.isEnabled = menu.itemCount > Resources.DEFAULT_MENU_COUNT
        }

        // set the sample fontmap action
        val sampleFontMapPaths = getSampleFontMapPaths("resource")
        if (sampleFontMapPaths?.size ?: 0 > 0) {
            val menu = MenuHandler.menuHandler.getMenu(Resources.MENU_SAMPLES_NAME)
            for (reopenItem in sampleFontMapPaths!!) {
                addSampleFontMapMenuItem(menu!!, reopenItem)
            }
        }
    }

    override fun threadRunStarted(threadEvent: ThreadEvent) {
        val progressBar = statusPanel.progressBar
        progressBar.minimum = 0
        progressBar.isIndeterminate = true
    }

    override fun threadRunning(threadEvent: ThreadEvent) {
        //TODO: set the progress bar maximum only once, typically when the thread starts
        val progressBar = statusPanel.progressBar
        progressBar.maximum = threadEvent.eventSource.progressMaximum
        progressBar.value = threadEvent.eventSource.progress
    }

    override fun threadRunFailed(threadEvent: ThreadEvent) {
        JOptionPane.showMessageDialog(this, threadEvent.eventSource.message)
        val progressBar = statusPanel.progressBar
        progressBar.value = 0
        progressBar.isIndeterminate = false
    }

    override fun threadRunFinished(threadEvent: ThreadEvent) {
        val progressBar = statusPanel.progressBar
        progressBar.value = 0
        progressBar.isIndeterminate = false
        val source = threadEvent.eventSource as FileReaderThread?
        statusPanel.setStatus("Read File: " + source!!.file)
    }

    /**
     * @param e ChangeEvent published by TabDesktop
     */
    override fun stateChanged(e: ChangeEvent) {
        val desktop = e.source as TabDesktop
        val index = desktop.selectedIndex
        if (index > -1) {
            val desktopFrame = desktop.getComponentAt(
                index
            ) as DesktopFrame
            desktopFrame.inputFileViewer.addThreadListener(this)
            desktopFrame.outputFileViewer.addThreadListener(this)

            // update the lock icon
            val desktopModel = desktopFrame.desktopModel
            val fontMap = desktopModel.fontMap
            fontMap.removeFontMapChangeListener(statusPanel)
            fontMap.addFontMapChangeListener(statusPanel)
            statusPanel.setLockFlag(!fontMap.isFileWritable)
            // update the clean/dirty flag
            statusPanel.setNeatness(fontMap)
            desktopFrame.mapperPanel.mappingEntryPanel.entryAction
                .addStatusListener(statusPanel)
            desktopFrame.mapperPanel.mappingEntryPanel.entryAction
                .addMessageListener(this)
            desktopFrame.mapperPanel.mappingEntryPanel
                .mappingTableModel.addMessageListener(this)

            // remove and add.. so getting added only once
            desktopFrame.mapperPanel.mappingEntryPanel.mappingTableModel
                .removeTableModelListener(statusPanel)
            desktopFrame.mapperPanel.mappingEntryPanel.listSelectionModel
                .removeListSelectionListener(statusPanel)
            desktopFrame.mapperPanel.mappingEntryPanel.mappingTableModel.addTableModelListener(statusPanel)
            desktopFrame.mapperPanel.mappingEntryPanel.listSelectionModel
                .addListSelectionListener(statusPanel)
        }
    }

    override fun messagePosted(messageEvent: MessageEvent) {
        JOptionPane.showMessageDialog(this, messageEvent.message)
    }

}