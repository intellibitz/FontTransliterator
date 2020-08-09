package sted.ui

import sted.STEDGUI.Companion.busy
import sted.STEDGUI.Companion.relax
import sted.actions.RedoAction
import sted.actions.UndoAction
import sted.event.*
import sted.fontmap.FontMap
import sted.io.FileFilterHelper
import sted.io.FileHelper.openFile
import sted.io.FontMapReader
import sted.io.Resources
import sted.io.Resources.cleanIcon
import sted.io.Resources.dirtyIcon
import sted.io.Resources.getResource
import sted.io.Resources.sTEDIcon
import sted.ui.MenuHandler.Companion.addReOpenItem
import sted.ui.MenuHandler.Companion.menuHandler
import sted.widgets.ButtonTabComponent
import java.awt.HeadlessException
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyVetoException
import java.io.File
import java.io.IOException
import java.util.*
import java.util.logging.Logger
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.InternalFrameEvent
import javax.swing.event.InternalFrameListener
import javax.xml.transform.TransformerException

/**
 * Desktop managing FontMap Internal Frame's
 */
class TabDesktop : JTabbedPane(), InternalFrameListener, IThreadListener, FontMapChangeListener, ChangeListener,
    ActionListener, IStatusEventSource, IMessageEventSource {
    private val desktopPane = JDesktopPane()
    private val frameNumberIndex = FrameNumberIndex()

    // cache to hold fonmap internal frames used by this desktop
    private val frameCache: MutableMap<String, DesktopFrame?> = HashMap()

    // cache to hold new/open fontmaps
    //    private Map<String, FontMap> fontMapCache = new HashMap<String, FontMap>();
    // Tabbed Desktop
    //    private JTabbedPane desktopFrames;
    //    private DesktopFrame fontMapperDesktopFrame;
    // clipboard for fontmap edits
    private val clipboard: MutableMap<String, Collection<*>> = HashMap()
    private var statusListener: IStatusListener? = null
    private var statusEvent: StatusEvent? = null
    private var messageListener: IMessageListener? = null
    private var messageEvent: MessageEvent? = null
    fun init() {
        // we need to listen to our own events here
        // to update the desktop
        addChangeListener(this)
        statusEvent = StatusEvent(this)
        messageEvent = MessageEvent(this)
        isVisible = true
    }

    fun load() {}
    fun fireStatusPosted(message: String?) {
        statusEvent!!.status = message
        statusListener!!.statusPosted(statusEvent!!)
    }

    override fun fireStatusPosted() {
        statusListener!!.statusPosted(statusEvent!!)
    }

    override fun addStatusListener(statusListener: IStatusListener) {
        this.statusListener = statusListener
    }

    fun addTab(desktopFrame: DesktopFrame) {
        addTab(desktopFrame.title, desktopFrame)
    }

    fun addTab(title: String, desktopFrame: DesktopFrame) {
        desktopFrame.title = title
        desktopFrame.removeInternalFrameListener(this)
        desktopFrame.addInternalFrameListener(this)
        desktopFrame.model.removeFontMapChangeListener(this)
        desktopFrame.model.addFontMapChangeListener(this)
        super.addTab(title, sTEDIcon, desktopFrame, getResource("tip.tab.fontmap"))
        initTabComponent(tabCount - 1, title)
        setEnabledAt(tabCount - 1, true)
        selectedIndex = tabCount - 1
        //        setSelectedComponent(desktopFrame);
//        desktopPane.setSelectedFrame(desktopFrame);
        try {
            desktopFrame.isSelected = true
        } catch (e: PropertyVetoException) {
            e.printStackTrace()
        }
        //        stedWindow.selectTab();
//        stedWindow.setState(JFrame.MAXIMIZED_BOTH);
//        stedWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.updateUI();
    }

    private fun initTabComponent(i: Int, title: String) {
        val buttonTabComponent = ButtonTabComponent("TabComponent $i", this)
        buttonTabComponent.tabTitle.icon = cleanIcon
        buttonTabComponent.tabTitle.text = title
        buttonTabComponent.addActionListener(this)
        setTabComponentAt(i, buttonTabComponent)
    }

    @JvmOverloads
    fun closeFontMap(desktopFrame: DesktopFrame = fontMapperDesktopFrame): Int {
        val i = saveDirty(desktopFrame)
        if (JOptionPane.CANCEL_OPTION != i) {
            var index = selectedIndex
            // todo: hacking because the first frame not getting selected by default
            if (index == -1) {
                index = tabCount - 1
            }
            removeTabFrameAt(index)
            desktopFrame.close()
        }
        // todo: this must be in activated
        // hacking it for now since the first frame we load by default is not
        // getting activated
        enableCloseAction()
        return i
    }

    fun closeFontMap(desktopFrame: DesktopFrame, i: Int): Int {
        val result = saveDirty(desktopFrame)
        if (JOptionPane.CANCEL_OPTION != result) {
            removeTabFrameAt(i)
            desktopFrame.close()
        }
        // todo: this must be in activated
        // hacking it for now since the first frame we load by default is not
        // getting activated
        enableCloseAction()
        return i
    }

    private fun removeTabFrameAt(index: Int): Boolean {
        if (index > -1) {
            val desktopFrame = getComponentAt(index) as DesktopFrame
            val title = desktopFrame.title
            if (title.startsWith(Resources.ACTION_FILE_NEW_COMMAND)) {
                val indx = getNewIndexNumber(title)
                // removes the tab index as generated by the newframeindex
                frameNumberIndex.removeIndex(indx)
            }
            // remove the tab with the tab index supplied
            removeTabAt(index)
            return true
        }
        return false
    }

    private fun getNewIndexNumber(title: String): Int {
        return Integer.valueOf(title.substring(
            Resources.ACTION_FILE_NEW_COMMAND.length
        ).trim { it <= ' ' })
    }

    private fun createNewFrameTitle(num: Int): String {
        return Resources.ACTION_FILE_NEW_COMMAND + " " +
                frameNumberIndex.addNewIndex(num)
    }

    override fun stateChanged(e: FontMapChangeEvent) {

        // select a new tab, if the fontmap is new
        val desktopFrame = selectedFrame
        val title = desktopFrame.title
        if (null != title) {
            val indx = indexOfTab(title)
            if (-1 == indx) {
                addTab(desktopFrame)
            } else {
                setEnabledAt(indx, true)
            }
        } else {
            addTab(desktopFrame)
        }
        // existing fontmaps, with existing tabs
        val buttonTabComponent = getTabComponentAt(selectedIndex) as ButtonTabComponent
        buttonTabComponent.tabTitle.icon = dirtyIcon
        val fontMap = desktopFrame.model.fontMap
        buttonTabComponent.tabTitle.text = fontMap.fontMapFile.name
        if (fontMap.isDirty) {
            buttonTabComponent.tabTitle.icon = dirtyIcon
        } else {
            buttonTabComponent.tabTitle.icon = cleanIcon
        }
        fireStatusPosted(
            buttonTabComponent.tabTitle.text + " Active"
        )
        //        buttonTabComponent.updateUI();
        updateUI()
    }

    /**
     * We are listening to our own events, so we can update the dependent
     * desktop. This is required since the tabbed pane acts as the proxy for the
     * contained desktop pane.
     *
     * @param e The change event of this tabbed pane
     */
    override fun stateChanged(e: ChangeEvent) {
        val me = e.source as TabDesktop
        var i = me.selectedIndex
        if (i != -1) {
            val myframe = getComponentAt(i) as DesktopFrame
            desktopPane.selectedFrame = myframe
            i = me.selectedIndex
            var title = myframe.title
            val buttonTab = getTabComponentAt(i)
            if (buttonTab is ButtonTabComponent)
                title = buttonTab.tabTitle.text
            fireStatusPosted("$title Active")
        }
        enableCloseAction()
    }

    private fun enableCloseAction(flag: Boolean) {
        menuHandler.getMenuItem("Close")?.setEnabled(flag)
    }

    private fun enableCloseAction() {
        if (tabCount > 0) {
            enableCloseAction(true)
        } else {
            enableCloseAction(false)
        }
    }

    /**
     * listens to tab button, so when tab button is used for closing sync it
     * with desktop
     *
     * @param e
     */
    override fun actionPerformed(e: ActionEvent) {
        val buttonTabComponent = e.source as ButtonTabComponent
        val i = indexOfTabComponent(buttonTabComponent)
        if (i != -1) {
            val desktopFrame = getComponentAt(i) as DesktopFrame
            //            closeFrameTab(desktopFrame, i-1);
            closeFontMap(desktopFrame, i)
        }
    }

    //hack!!
    //todo: the frame must be selected, so we dont do this
//        desktopFrame = (DesktopFrame) getComponentAt(getTabCount()-1);
    private val selectedFrame: DesktopFrame
        get() =//hack!!
        //todo: the frame must be selected, so we dont do this
//        desktopFrame = (DesktopFrame) getComponentAt(getTabCount()-1);
            desktopPane.selectedFrame as DesktopFrame
    /*
    private void closeFrameTab (DesktopFrame fontMapperDesktopFrame, int i)
    {
        if (JOptionPane.CANCEL_OPTION !=
                closeFontMap(fontMapperDesktopFrame))
        // cancel button has been pressed during save operation
        // safely return without performing close
        {
            removeTabFrameAt(i);
        }
    }
*/
    /**
     * sets FontMap and invokes #fireFontMapChangeEvent ()
     *
     * @param desktopFrame
     */
    fun addListenersToDesktopFrame(
        desktopFrame: DesktopFrame?
    ) {
        val mapperPanel = desktopFrame!!.mapperPanel
        val desktopModel = desktopFrame.model
        val fontMap = desktopModel.fontMap
        fontMap.removeFontMapChangeListener(
            mapperPanel
        )
        fontMap.addFontMapChangeListener(
            mapperPanel
        )
        fontMap.removeFontMapChangeListener(
            mapperPanel.mappingEntryPanel
        )
        fontMap.addFontMapChangeListener(
            mapperPanel.mappingEntryPanel
        )
        fontMap.removeFontMapChangeListener(desktopFrame)
        fontMap.addFontMapChangeListener(desktopFrame)
        val menuHandler = menuHandler
        val actions: Map<*, *> = menuHandler.actions
        val newAction = menuHandler.getAction(Resources.ACTION_FILE_NEW_COMMAND) as FontMapChangeListener?
        fontMap.removeFontMapChangeListener(newAction)
        fontMap.addFontMapChangeListener(newAction)
        val reload = actions[Resources.ACTION_FILE_RELOAD] as FontMapChangeListener?
        fontMap.removeFontMapChangeListener(reload)
        fontMap.addFontMapChangeListener(reload)
        val save = actions[Resources.ACTION_FILE_SAVE_COMMAND] as FontMapChangeListener?
        fontMap.removeFontMapChangeListener(save)
        fontMap.addFontMapChangeListener(save)
        val paste = actions[Resources.ACTION_PASTE_COMMAND] as FontMapChangeListener?
        fontMap.removeFontMapChangeListener(paste)
        fontMap.addFontMapChangeListener(paste)
        val undo = actions[Resources.ACTION_UNDO_COMMAND] as UndoAction?
        fontMap.removeUndoListener(undo)
        undo!!.isEnabled = false
        fontMap.addUndoListener(undo)
        removeChangeListener(undo)
        addChangeListener(undo)
        val redo = actions[Resources.ACTION_REDO_COMMAND] as RedoAction?
        fontMap.removeRedoListener(redo)
        redo!!.isEnabled = false
        fontMap.addRedoListener(redo)
        removeChangeListener(redo)
        addChangeListener(redo)
        val keypad1 = mapperPanel.fontKeypad1
        fontMap.removeFontListChangeListener(keypad1)
        fontMap.addFontListChangeListener(keypad1)
        desktopModel.addFontMapChangeListener(keypad1)
        val keypad2 = mapperPanel.fontKeypad2
        fontMap.removeFontListChangeListener(keypad2)
        fontMap.addFontListChangeListener(keypad2)
        desktopModel.addFontMapChangeListener(keypad2)
        desktopFrame.addInternalFrameListener(
            menuHandler.actions[Resources.ACTION_FILE_NEW_COMMAND] as InternalFrameListener?
        )
        desktopFrame.addInternalFrameListener(
            menuHandler.actions[Resources.ACTION_FILE_RELOAD_COMMAND] as InternalFrameListener?
        )
        desktopFrame.addInternalFrameListener(
            menuHandler.actions[Resources.ACTION_FILE_REOPEN_COMMAND] as InternalFrameListener?
        )
        desktopFrame.addInternalFrameListener(
            menuHandler.actions[Resources.ACTION_FILE_SAVEAS_COMMAND] as InternalFrameListener?
        )
        desktopFrame.addInternalFrameListener(
            menuHandler.actions[Resources.ACTION_FILE_CLOSE_COMMAND] as InternalFrameListener?
        )
        desktopModel.removeFontMapChangeListener(newAction)
        desktopModel.addFontMapChangeListener(newAction)
        val reopen = menuHandler.actions[Resources.ACTION_FILE_REOPEN_COMMAND] as FontMapChangeListener?
        desktopModel.removeFontMapChangeListener(reopen)
        desktopModel.addFontMapChangeListener(reopen)
        desktopFrame
            .addInternalFrameListener(reopen as InternalFrameListener?)
    }

    fun createDesktopModel(desktopFrame: DesktopFrame, fontMap: FontMap): DesktopModel {
        val desktopModel = DesktopModel()
        desktopModel.fontMap = fontMap
        desktopFrame.model = desktopModel
        addListenersToDesktopFrame(desktopFrame)
        desktopFrame.load()
        return desktopModel
    }

    fun loadFontMap(file: File) {
        loadFontMap(selectedFrame, file)
    }

    fun loadFontMap(desktopFrame: DesktopFrame, file: File) {
        val desktopModel = createDesktopModel(
            desktopFrame, FontMap(file)
        )
        desktopModel.addFontMapChangeListener(this)
        readFontMap(desktopModel)
        fireStatusPosted("FontMap loaded")
    }

    /**
     * reads the FontMap from an external File
     *
     * @param desktopModel
     */
    fun readFontMap(desktopModel: DesktopModel) {
        //TODO: move this somewhere applicable
//        clear();
//        final String path1 = fontMap.getFont1Path();
//        final String path2 = fontMap.getFont2Path();
//        fontMap.clear();

//        fontMap.clearAll();

//        fontMap.setFontMapFile(file);
//        fontMap.setFont1Path(path1);
//        fontMap.setFont2Path(path2);
        val fontMap = desktopModel.fontMap
        try {
            val fontMapReader = FontMapReader(fontMap)
            fontMapReader.addThreadListener(this)
            SwingUtilities.invokeLater(fontMapReader)
        } catch (e: IllegalArgumentException) {
            fireMessagePosted(
                "Cannot Read FontMap.. Failed: " + e.message
            )
            logger.severe(
                "Cannot Read FontMap - Illegal Argument " +
                        fontMap.fontMapFile.absolutePath
            )
            fireStatusPosted(
                "Cannot Read FontMap - Illegal Argument " +
                        fontMap.fontMapFile.absolutePath
            )
        }
    }

    /**
     *
     */
    fun openFontMap() {
        val selectedFile = openFile(
            "Please select FontMap location:", Resources.XML,
            "STED FontMap files", this
        )
        selectedFile?.let { openFontMap(it) }
    }

    fun newFontMap() {
        val desktopFrame = loadNewFontMap()
        val num = tabCount + 1
        addTab(
            createNewFrameTitle(num),
            desktopFrame
        )
        fireStatusPosted("New FontMap")
    }

    /**
     * creates a new FontMap and loads into the Window if the Window already
     * contains a new FontMap, clears and loads them otherwise, looks up the
     * cache for a new FontMap and load them if not in cache, creates a new
     * FontMap, stores in cache and loads new FontMap
     *
     * @return DesktopFrame the newly added fontmap component
     */
    fun loadNewFontMap(): DesktopFrame {
        val desktopFrame = createFontMapperDesktopFrame()
        val fontMap = FontMap()
        val desktopModel = createDesktopModel(desktopFrame, fontMap)
        //        fontMapCache.put(Resources.ACTION_FILE_NEW_COMMAND, fontMap);
        desktopModel.fireFontMapChangedEvent()
        return desktopFrame
    }

    fun reopenFontMap(fileName: String?) {
        openFontMap(File(fileName))
        /*
        FontMap fontMap = getFontMap();
        // add it to the reopen menu.. one last check
        // TODO: remove the following.. typically reopen items must be added as part of events
        // only fontmaps with a valid filename can be re-opened
        if (!fontMap.isNew())
        {
            addItemToReOpenMenu(fontMap.getFileName());
        }
        MenuHelper.disableMenuItem(MenuHandler.getInstance(), fileName);
        fireStatusPosted("FontMap Re-Opened");
*/
/*
        if (fontMap == null) {
            fontMap = new FontMap();
            addListenersToDesktopFrame(fontMap);
        } else {
            //TODO: must discard the old fontmap here
//                    fontMap.clearAll();
        }
        // add it to the reopen menu.. one last check
        // TODO: remove the following.. typically reopen items must be added as part of events
        // only fontmaps with a valid filename can be re-opened
        if (!fontMap.isNew()) {
            addItemToReOpenMenu(fontMap.getFileName());
        }
        // clear the old stuff, if any
        fontMap.clear();
//        fontMap.clearAll();
        fontMap.setFontMapFile(new File(fileName));
        MenuHelper.disableMenuItem(stedWindow.getInstance(), fileName);
        stedWindow.getDesktop().readFontMap();
*/
    }

    fun reloadFontMap() {
//        readFontMap(getSelectedFrame().getModel());
        val selectedFrame = selectedFrame
        val selectedFile = selectedFrame.model.fontMap.fontMapFile
        //        closeFontMap(selectedFrame);
        removeTabFrameAt(selectedIndex)
        frameCache.remove(selectedFrame.model.fontMap.fileName)
        //        fontMapCache
//                .remove(selectedFrame.getModel().getFontMap().getFileName());
        openFontMap(selectedFile)
    }

    fun openFontMap(selectedFile: File) {
        try {
            var desktopFrame = frameCache[selectedFile.absolutePath]
            if (desktopFrame == null) {
                desktopFrame = createFontMapperDesktopFrame()
                //todo: repeat the logic of the frame creation, init and load
                loadFontMap(desktopFrame, selectedFile)
                val desktopModel = desktopFrame.model
                val fontMap = desktopModel.fontMap
                //                desktopModel.setFontMap(fontMap);
//                fontMapCache.put(fontMap.getFileName(), fontMap);
                frameCache[fontMap.fileName] = desktopFrame
            }
            // add it to the tabs
            add(desktopFrame)
            desktopPane.selectedFrame = desktopFrame
            desktopFrame.model.fireFontMapChangedEvent()
            //            showDesktopFrame();
/*
                // try the cache first
                fontMap = (FontMap) fontMapCache.get(selectedFile.getAbsolutePath());
                // if not found.. create a new one
                if (fontMap == null) {
                    readFontMap(selectedFile);
                    fontMapCache.put(fontMap.getFileName(), fontMap);
                } else {
                    fireFontMapChangedEvent();
                    showDesktopFrame();
                }
*/
        } catch (ex: HeadlessException) {
            logger.throwing(
                "sted.util.FontMapHelper",
                "readFontMap", ex
            )
            JOptionPane.showMessageDialog(
                this,
                "Invalid FontMap " + selectedFile.absolutePath
            )
        } catch (ex: IllegalArgumentException) {
            logger.throwing(
                "sted.util.FontMapHelper",
                "readFontMap", ex
            )
            JOptionPane.showMessageDialog(
                this,
                "Load Failed: " + ex.message
            )
        }
    }

    private fun saveFontMap() {
        val desktopFrame = selectedFrame
        var fontMap = desktopFrame.model.fontMap
        fontMap.setFont1(
            desktopFrame.mapperPanel
                .fontKeypad1.selectedFont
        )
        fontMap.setFont2(
            desktopFrame.mapperPanel
                .fontKeypad2.selectedFont
        )
        try {
            fontMap = desktopFrame.model.saveFontMap()
            //add it to cache
//            fontMapCache.put(fontMap.getFileName(), fontMap);
//            get
        } catch (exception: TransformerException) {
            exception
                .printStackTrace() //To change body of catch statement use Options | File Templates.
            JOptionPane.showMessageDialog(
                this,
                fontMap.fileName +
                        " cannot create for writing " +
                        exception.message
            )
        }
        //        JOptionPane.showMessageDialog(stedWindow, "saved FontMap in " + selectedFile.getAbsolutePath());
    }

    fun saveAction() {
        val fontMap = fontMap
        val selectedFile = fontMap.fontMapFile
        if (!selectedFile.canWrite()) {
            try {
                selectedFile.createNewFile()
                fontMap.fontMapFile = selectedFile
            } catch (exception: IOException) {
                JOptionPane.showMessageDialog(
                    this,
                    selectedFile.toString() +
                            " cannot create for writing " +
                            exception.message
                )
            }
        } else {
            saveFontMap()
        }
    }

    fun saveAsAction(): Int {
        val fontMap = fontMap
        val jFileChooser = JFileChooser(System.getProperty("user.dir"))
        val fileFilterHelper = FileFilterHelper("xml", "STED FontMap files")
        jFileChooser.fileFilter = fileFilterHelper
        val result = jFileChooser.showSaveDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = jFileChooser.selectedFile
            var ok = false
            if (!selectedFile.canWrite()) {
                try {
                    ok = selectedFile.createNewFile()
                } catch (exception: IOException) {
                    JOptionPane.showMessageDialog(
                        this,
                        selectedFile.toString() +
                                " cannot create for writing " +
                                exception.message
                    )
                }
            }
            if (ok) {
                fontMap.fontMapFile = selectedFile
                saveFontMap()
            } else {
                JOptionPane.showMessageDialog(
                    this, selectedFile.toString() +
                            " is NOT Writable"
                )
            }
        }
        return result
    }

    @JvmOverloads
    fun saveDirty(desktopFrame: DesktopFrame? = selectedFrame): Int {
        var result = JOptionPane.CLOSED_OPTION
        if (null != desktopFrame) {
            val fontMap = desktopFrame.model.fontMap
            if (fontMap.isDirty) {
                result = JOptionPane.showConfirmDialog(
                    this,
                    "FontMap Changed.. Do you want to save changes?",
                    "Save Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION
                )
                // if yes, save the fontmap
                if (JOptionPane.YES_OPTION == result) {
                    if (fontMap.isNew) {
                        result = saveAsAction()
                        // can be cancelled.. clear the fontmap
                        if (JFileChooser.CANCEL_OPTION == result) {
                            fontMap.clear()
                        }
                    } else {
                        saveAction()
                    }
                } else if (JOptionPane.NO_OPTION == result) {
                    //TODO: need to do something sensible for NO_OPTION
                    // if no, discard the fontmap
                    // setDirty will fire events..so DONT use!
//                fontMap.setDirty(false);
                    // try clear (need to investigate if this would affect other actions!)
                    fontMap.clear()
                }
            }
        }
        return result
    }

    fun clear() {
        selectedFrame.clear()
    }

    val fontMap: FontMap
        get() = selectedFrame.model
            .fontMap

    fun addItemToReOpenMenu(item: String?) {
        val menuHandler = menuHandler
        val menu = menuHandler.getMenu(Resources.ACTION_FILE_REOPEN_COMMAND)
        addReOpenItem(menu!!, item)
        menu.isEnabled = menu.itemCount != Resources.DEFAULT_MENU_COUNT + 1
    }

    fun createFontMapperDesktopFrame(): DesktopFrame {
        val desktopFrame = DesktopFrame()
        desktopFrame.addInternalFrameListener(this)
        desktopFrame.init()
        // add it to the desktop
        desktopPane.add(desktopFrame)
        desktopPane.selectedFrame = desktopFrame
        desktopFrame.isEnabled = true
        desktopFrame.isVisible = true
        return desktopFrame
    }

    /**
     * @return
     */
    fun getClipboard(): Map<String, Collection<*>> {
        return clipboard
    }

    /**
     * creates a new FontMap and loads into the Window if the Window already
     * contains a new FontMap, clears and loads them otherwise, looks up the
     * cache for a new FontMap and load them if not in cache, creates a new
     * FontMap, stores in cache and loads new FontMap
     * @return
     */
    /*
    public void loadNewFontMap() {
        // FIRST TIME
        // create a new frame and set the fontmap
        // OTHER THAN FIRST TIME
        // get the frame from the cache, set it as current
        clear();
        // check the current FontMap first
        if (fontMap != null && fontMap.isNew()) {
            // clear the existing fontmap contents.. no need to clear the listeners
            fontMap.clear();
//                stedWindow.addListenersToDesktopFrame(fontMap);
//                stedWindow.createDesktopModel();
            fireFontMapChangedEvent();
        } else {
            // try the cache
            FontMap fMap = (FontMap) fontMapCache.get(Resources.ACTION_FILE_NEW_COMMAND);
            // if not found, create new one
            if (fMap == null) {
                fMap = new FontMap();
                fontMapCache.put(Resources.ACTION_FILE_NEW_COMMAND, fMap);
                addListenersToDesktopFrame(fMap);
            } else if (!fMap.isNew()) {
                fMap = new FontMap();
                fontMapCache.put(Resources.ACTION_FILE_NEW_COMMAND, fMap);
                addListenersToDesktopFrame(fMap);
            } else {
                fontMap.clear();
//                stedWindow.addListenersToDesktopFrame(fontMap);
//                stedWindow.createDesktopModel();
                fireFontMapChangedEvent();
            }
        }
        stedWindow.showDesktop();
    }
*/
    fun addToClipboard(entry: String, value: Collection<*>) {
        clipboard[entry] = value
        fireStatusPosted("Copied Fontmap Entries")
    }

    val fontMapperDesktopFrame: DesktopFrame
        get() = selectedFrame
    val desktopModel: DesktopModel
        get() = fontMapperDesktopFrame.model
    val frameTitle: String?
        get() {
            val desktopFrame = selectedFrame
            return desktopFrame.title
        }

    fun fireMessagePosted(message: String?) {
        messageEvent!!.message = message
        messageListener!!.messagePosted(messageEvent!!)
    }

    override fun fireMessagePosted() {
        messageListener!!.messagePosted(messageEvent!!)
    }

    override fun addMessageListener(messageListener: IMessageListener) {
        this.messageListener = messageListener
    }

    override fun threadRunStarted(threadEvent: ThreadEvent) {
        busy()
    }

    override fun threadRunning(threadEvent: ThreadEvent) {}
    override fun threadRunFailed(threadEvent: ThreadEvent) {
        busy()
        val message = threadEvent.eventSource.message.toString()
        JOptionPane.showMessageDialog(
            this, message, "Error",
            JOptionPane.ERROR_MESSAGE
        )
        // if FontMapReader..
        if (FontMapReadEvent::class.java.isInstance(threadEvent)) {
            closeFontMap()
        }
        // if Transliterator..
        if (TransliterateEvent::class.java.isInstance(threadEvent)) {
            // enable the convert action
            menuHandler
                .getAction(Resources.ACTION_CONVERT_NAME)
                ?.setEnabled(true)
            // disable the stop button
            menuHandler.getAction(Resources.ACTION_STOP_NAME)
                ?.setEnabled(false)
        }
        fireStatusPosted(message)
        relax()
    }

    override fun threadRunFinished(threadEvent: ThreadEvent) {
        // if FontMapReader..
        if (FontMapReadEvent::class.java.isInstance(threadEvent)) {
            val desktopModel = selectedFrame.model
            val fontMap = desktopModel.fontMap
            fontMap.isDirty = false
            desktopModel.fireFontMapChangedEvent()
            fireStatusPosted("FontMap Loaded")
        } else if (TransliterateEvent::class.java.isInstance(threadEvent)) {
            // readFontMap the converted file
            selectedFrame.readOutputFile()
            // enable the convert action
            menuHandler
                .getAction(Resources.ACTION_CONVERT_NAME)
                ?.setEnabled(true)
            // disable the stop button
            menuHandler.getAction(Resources.ACTION_STOP_NAME)
                ?.setEnabled(false)
            fireStatusPosted("Transliterate Done")
        }
        relax()
    }

    override fun internalFrameClosing(e: InternalFrameEvent) {
        val desktopFrame = e.internalFrame as DesktopFrame
        if (JOptionPane.CANCEL_OPTION != saveDirty(desktopFrame)) {
            // when the frame closes, close the tab
            val index = selectedIndex
            removeTabFrameAt(index)
            desktopFrame.close()
        }
        // todo: this must be in activated
        // hacking it for now since the first frame we load by default is not
        // getting activated
        enableCloseAction()
    }

    override fun internalFrameActivated(e: InternalFrameEvent) {
        // enable the view mapping and sample when the internal frame is shown
        menuHandler
            .getMenuItem(Resources.ACTION_VIEW_MAPPING)
            ?.setEnabled(true)
        menuHandler
            .getMenuItem(Resources.ACTION_VIEW_SAMPLE)
            ?.setEnabled(true)
        val desktopFrame = e.internalFrame as DesktopFrame
        desktopFrame.setEnabledFontMapTab(true)
        enableCloseAction()
    }

    override fun internalFrameOpened(e: InternalFrameEvent) {
        // todo: this must be in activated
        // hacking it for now since the first frame we load by default is not
        // getting activated
        enableCloseAction()
    }

    override fun internalFrameClosed(e: InternalFrameEvent) {
        // todo: this must be in activated
        // hacking it for now since the first frame we load by default is not
        // getting activated
        enableCloseAction()
    }

    override fun internalFrameIconified(e: InternalFrameEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    override fun internalFrameDeiconified(e: InternalFrameEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    override fun internalFrameDeactivated(e: InternalFrameEvent) {}

    /**
     * FrameNumberIndex <br></br> Generates Unique Id for New fontmap frames
     */
    private class FrameNumberIndex {
        private val indices: MutableSet<Int> = TreeSet()

        /**
         * if the index is new, add it if the index is existing, then find free
         * index and add it
         *
         * @param indx
         * @return
         */
        fun addNewIndex(indx: Int): Int {
            val sz = indices.size
            if (!containsIndex(indx) && indx >= sz) {
                indices.add(indx)
                return indx
            } else {
                for (i in 1..sz) {
                    if (!containsIndex(i)) {
                        indices.add(i)
                        return i
                    }
                }
            }
            return indx
        }

        fun removeIndex(indx: Int): Boolean {
            return indices.remove(indx)
        }

        fun containsIndex(indx: Int): Boolean {
            for (indice in indices) {
                if (indice == indx) {
                    return true
                }
            }
            return false
        }
    }

    companion object {
        private val logger = Logger.getLogger(TabDesktop::class.java.name)
    }
}