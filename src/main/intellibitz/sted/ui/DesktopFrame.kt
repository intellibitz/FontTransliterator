package sted.ui

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.event.FontMapEntriesChangeEvent
import sted.event.IFontMapEntriesChangeListener
import sted.fontmap.FontMap
import sted.io.Resources
import sted.io.Resources.cleanIcon
import sted.io.Resources.dirtyIcon
import sted.io.Resources.getResource
import sted.io.Resources.getSystemResourceIcon
import sted.io.Resources.sTEDIcon
import java.beans.PropertyVetoException
import java.io.File
import java.util.logging.Logger
import javax.swing.BorderFactory
import javax.swing.JInternalFrame
import javax.swing.JTabbedPane
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener

/**
 * Contains FontMap and all its related Entities.
 */
class DesktopFrame : JInternalFrame(), TableModelListener, FontMapChangeListener, IFontMapEntriesChangeListener {
    // to hold all the different tabs.. FontMap, Input File Viewer, Output etc.,
    private val tabbedPane = JTabbedPane(JTabbedPane.BOTTOM)

    // panel showing the FontMap
    val mapperPanel = MapperPanel()

    // input editor pane
    val inputFileViewer = FileViewer()

    // output editor pane
    val outputFileViewer = FileViewer()
    val desktopModel = DesktopModel()
    fun init() {
        setTitle("FontMapperInternalFrame")
        isResizable = false
        isClosable = true
        isMaximizable = false
        isIconifiable = false
        //        mapperPanel = new MapperPanel();
        mapperPanel.init()
        setNormalIcon()
        //        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        tabbedPane.border = BorderFactory.createRaisedBevelBorder()
        // adds the font mapper panel as one of the tab
        tabbedPane.addTab(
            getResource(Resources.TITLE_TAB_FONTMAP),
            sTEDIcon,
            mapperPanel,
            getResource(Resources.TIP_TAB_FONTMAP)
        )
        inputFileViewer.frameIcon = getSystemResourceIcon(getResource("icon.file.input"))

        // adds the input file viewer as one of the tab
        tabbedPane.addTab(
            getResource(Resources.TITLE_TAB_INPUT),
            getSystemResourceIcon(
                getResource(
                    Resources.ICON_FILE_INPUT
                )
            ),
            inputFileViewer
        )
        outputFileViewer.frameIcon =
            getSystemResourceIcon(getResource("icon.file.output"))

        // adds the output file viewer as one of the tab
        tabbedPane.addTab(
            getResource(Resources.TITLE_TAB_OUTPUT),
            getSystemResourceIcon(
                getResource(
                    Resources.ICON_FILE_OUTPUT
                )
            ),
            outputFileViewer
        )

        // enables all the tabs
        tabbedPane.setEnabledAt(0, false)
        tabbedPane.setEnabledAt(1, false)
        tabbedPane.setEnabledAt(2, false)
        contentPane.add(tabbedPane)
        bounds = mapperPanel.bounds
        location = mapperPanel.location
        pack()
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        isVisible = true
    }

    fun load() {
        desktopModel.addFontMapChangeListener(this)
        desktopModel.addFontMapChangeListener(mapperPanel)
        desktopModel.addFontMapChangeListener(mapperPanel.mappingEntryPanel)
        desktopModel.addFontMapChangeListener(
            mapperPanel.mappingEntryPanel.mappingRules
        )
        mapperPanel.load()
        mapperPanel.mappingEntryPanel.addTableModelListener(this)
        mapperPanel.outputText.fontMap = desktopModel.fontMap
        mapperPanel.setSampleInput(
            getResource(Resources.SAMPLE_INPUT_TEXT)
        )
        tabbedPane.setToolTipTextAt(1, MenuHandler.toolTips["Input"])
        tabbedPane.setToolTipTextAt(2, MenuHandler.toolTips["Output"])
    }

    private fun setNormalIcon() {
        setFrameIcon(cleanIcon)
    }

    private fun setEditIcon() {
        setFrameIcon(dirtyIcon)
    }

    fun clear() {
        mapperPanel.clear()
        desktopModel.clear()
        setTitle(Resources.EMPTY_STRING)
        setNormalIcon()
    }

    private fun hideInternalFrameSelected() {
        try {
            setSelected(false)
        } catch (e: PropertyVetoException) {
            logger.throwing(javaClass.name, "hideInternalFrameSelected", e)
            e.printStackTrace() //To change body of catch statement use Options | File Templates.
        }
    }

    /*
    public void showFrame() {
        setVisible(true);
        tabbedPane.setVisible(true);
//        tabbedPane.setSelectedIndex(0);
        setFrameTitle(desktopModel.getFontMap());
        try {
            setMaximum(true);
        } catch (PropertyVetoException e) {
            logger.throwing(getClass().getName(), "showFrame", e);
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        mapperPanel.getMappingEntryPanel().getWord1().requestFocus();
//        setBounds(tabbedPane.getBounds());
    }
*/
    private fun hideFrame() {
        hideInternalFrameSelected()
        isVisible = false
    }

    /**
     * @param flag boolean to enable or disable the tabs
     */
    fun enableTabs(flag: Boolean) {
        val count = tabbedPane.tabCount
        if (count > 0) {
            for (i in 0 until count) {
                tabbedPane.setEnabledAt(i, flag)
            }
        }
        //        tabbedPane.setVisible(flag);
    }

    fun close() {
        hideFrame()
        enableTabs(false)
        MenuHandler.menus[Resources.ACTION_VIEW_MAPPING]?.isEnabled = false
        MenuHandler.menus[Resources.ACTION_VIEW_SAMPLE]?.isEnabled = false
        MenuHandler.menus[Resources.ACTION_PASTE_COMMAND]?.isEnabled = false
        MenuHandler.menus[Resources.ACTION_DELETE_COMMAND]?.isEnabled = false
        MenuHandler.menus[Resources.ACTION_SELECT_ALL_COMMAND]?.isEnabled = false
        MenuHandler.menus[Resources.ACTION_CUT_COMMAND]?.isEnabled = false
        MenuHandler.menus[Resources.ACTION_COPY_COMMAND]?.isEnabled = false
    }

    fun setEnabledFontMapTab(flag: Boolean) {
        if (tabbedPane.tabCount > 0) {
            tabbedPane.setEnabledAt(0, flag)
        }
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        if (desktopModel.fontMap.isDirty) {
            setEditIcon()
        }
    }

    private fun setFileIcon(fontMap: FontMap) {
        if (fontMap.isDirty) {
            setEditIcon()
        } else {
            setNormalIcon()
        }
    }

    fun setFrameTitle(fontMap: FontMap) {
        setTitle(fontMap.fontMapFile.absolutePath)
    }

    /**
     * called when the FontMap state is changed.. typically when a FontMapEntry
     * is added to FontMap
     *
     * @param fontMapChangeEvent FontMapChangeEvent
     */
    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        val fontMap = fontMapChangeEvent.fontMap
        setFrameTitle(fontMap)
        setFileIcon(fontMap)
        inputFileViewer.font = fontMap.font1
        outputFileViewer.font = fontMap.font2
        enableConverterIfFilesLoaded()
    }

    fun enableConverterIfFilesLoaded(): Boolean {
        val flag = desktopModel.isReadyForTransliteration
        MenuHandler.actions[Resources.ACTION_CONVERT_NAME]!!.isEnabled = flag
        return flag
    }

    fun setInputFile(file: File?) {
        desktopModel.inputFile = file!!
        readFile(1)
    }

    private fun readFile(index: Int) {
//        STEDGUI.busy();
        tabbedPane.setEnabledAt(index, true)
        tabbedPane.selectedIndex = index
        when (index) {
            1 -> {
                inputFileViewer.setFileName(
                    desktopModel.inputFile.absolutePath
                )
                inputFileViewer.readFile()
            }
            2 -> readOutputFile()
            else -> {
            }
        }
        //        STEDGUI.relax();
    }

    fun setOutputFile(file: File?) {
        desktopModel.outputFile = file!!
        readFile(2)
    }

    //TODO: change this to private and add this as the respective listener
    fun readOutputFile() {
        outputFileViewer.setFileName(
            desktopModel.outputFile.absolutePath
        )
        outputFileViewer.readFile()
    }

    override fun stateChanged(fontMapEntriesChangeEvent: FontMapEntriesChangeEvent) {
        desktopModel.fireFontMapChangedEvent()
    }

    companion object {
        private val logger = Logger.getLogger("sted.ui.DesktopFrame")
    }
}