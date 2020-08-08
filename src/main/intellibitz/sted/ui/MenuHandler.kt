package sted.ui

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import sted.actions.*
import sted.fontmap.FontMap
import sted.io.Resources
import sted.io.Resources.getResource
import sted.io.Resources.getSystemResourceIcon
import java.awt.event.ItemListener
import java.io.IOException
import java.util.*
import java.util.logging.Logger
import javax.swing.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

class MenuHandler private constructor() : DefaultHandler() {
    private var menuBar: JMenuBar? = null
    private var toolBar: JToolBar? = null
    private var popupMenu: JPopupMenu? = null

    @Throws(SAXException::class, ParserConfigurationException::class, IOException::class)
    private fun loadMenu(xml: String?) {
        val saxParserFactory = SAXParserFactory.newInstance()
        saxParserFactory.isValidating = true
        val saxParser = saxParserFactory.newSAXParser()
        saxParser.parse(ClassLoader.getSystemResourceAsStream(xml), this)
    }

    fun getMenuBar(name: String): JMenuBar? {
        return menuBars[name]
    }

    fun getToolBar(name: String): JToolBar? {
        return toolBars[name]
    }

    val menuItems: Map<String, JMenuItem?>
        get() = Companion.menuItems
    val tooltips: Map<String, String>
        get() = toolTips
    val actions: Map<String, Action>
        get() = Companion.actions
    val imageIcons: Map<String, ImageIcon?>
        get() = Resources.imageIcons

    fun getAction(name: String): Action? {
        return Companion.actions[name]
    }

    fun getToolButton(name: String): AbstractButton? {
        return toolButtons[name]
    }

    fun getMenu(name: String): JMenu? {
        return menus[name]
    }

    fun getMenuItem(name: String): JMenuItem? {
        return Companion.menuItems[name]
    }

    fun removeMenuItem(name: String) {
        Companion.menuItems.remove(name)
    }

    fun addMenuItem(menuItem: JMenuItem) {
        if (!Companion.menuItems.containsKey(menuItem.name)) {
            Companion.menuItems[menuItem.name] = menuItem
        }
    }

    fun getPopupMenu(name: String): JPopupMenu? {
        return popupMenus[name]
    }

    @Throws(SAXException::class)
    override fun startElement(
        uri: String, localName: String,
        qName: String, attributes: Attributes
    ) {
        if ("menubar" == qName) {
            menuBar = JMenuBar()
            menuBar!!.name = attributes.getValue("name")
            val toolBarName = attributes.getValue("toolBarName")
            toolBar = toolBars[toolBarName]
            if (toolBar == null) {
                toolBar = JToolBar(JToolBar.HORIZONTAL)
                toolBar!!.name = toolBarName
            }
        } else if ("menu" == qName) {
            try {
                stack.push(createMenu(attributes))
            } catch (e: Exception) {
                logger.severe("Unable to create Menu Item: " + e.message)
                e.printStackTrace() //To change body of catch statement use Options | File Templates.
            }
        } else if ("popup_menu" == qName) {
            popupMenu = createPopupMenu(attributes)
        } else if ("menuitem" == qName) {
            if (popupMenu == null) {
                val menu = stack.peek()
                menu.add(createMenuItem(attributes))
            } else {
                popupMenu!!.add(createMenuItem(attributes))
            }
        } else if ("menuitemref" == qName) {
            if (popupMenu == null) {
                val menu = stack.peek()
                menu.add(createMenuItemRef(attributes))
            } else {
                popupMenu!!.add(createMenuItemRef(attributes))
            }
        } else if ("seperator" == qName) {
            if (popupMenu == null) {
                val menu = stack.peek()
                menu.addSeparator()
            } else {
                popupMenu!!.addSeparator()
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if ("menubar" == qName) {
            menuBars[menuBar!!.name] = menuBar
            // moved from getToolBar block
            toolBar!!.orientation = JToolBar.HORIZONTAL
            toolBar!!.isFloatable = false
            toolBar!!.isRollover = true
            toolBar!!.add(Box.createVerticalGlue())
            //
            toolBars[toolBar!!.name] = toolBar
        } else if ("menu" == qName) {
            val menu = stack.pop()
            if (stack.isEmpty()) {
                toolBar!!.add(Box.createHorizontalStrut(5))
                menuBar!!.add(menu)
            } else {
                val parent = stack.peek()
                parent.add(menu)
            }
            menus[menu.name] = menu
        } else if ("popup_menu" == qName) {
            popupMenus[popupMenu!!.name] = popupMenu
        }
    }

    @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
    private fun createMenu(attributes: Attributes): JMenu {
        val menu = JMenu()
        val name = attributes.getValue("name")
        menu.name = name
        menu.text = name
        val mnemonic = attributes.getValue("mnemonic")
        menu.setMnemonic(mnemonic[0])
        val actionName = attributes.getValue("action")
        if (null != actionName) {
            val action = newActionInstance(name)
            action?.putValue(Action.NAME, name)
            action?.putValue(Action.MNEMONIC_KEY, mnemonic[0].toInt())
            menu.action = action
            Companion.actions[name] = action!!
        }
        menu.isEnabled = java.lang.Boolean.parseBoolean(attributes.getValue("actionEnabled"))
        return menu
    }

    private fun createPopupMenu(attributes: Attributes): JPopupMenu {
        val menu = JPopupMenu(attributes.getValue("name"))
        menu.name = attributes.getValue("name")
        return menu
    }

    private fun createMenuItemRef(attributes: Attributes): JMenuItem {
        val menuItem = getMenuItem(attributes.getValue("name"))
        val cloned = JMenuItem(menuItem!!.action)
        cloned.name = menuItem.name
        cloned.text = menuItem.text
        cloned.isSelected = menuItem.isSelected
        cloned.horizontalTextPosition = menuItem.horizontalTextPosition
        val itemListeners = menuItem.itemListeners
        if (itemListeners != null) {
            for (itemListener in itemListeners) {
                cloned.addItemListener(itemListener)
            }
        }
        return cloned
    }

    private fun createMenuItem(attributes: Attributes): JMenuItem? {
        var menuItem: JMenuItem? = null
        try {
            val name = attributes.getValue("name")
            val type = attributes.getValue("type")
            val ic = attributes.getValue("icon")
            val tooltip = attributes.getValue("tooltip")
            val shortcut = attributes.getValue("mnemonic")
            toolTips[name] = tooltip
            val value = attributes.getValue("action")
            val action = newActionInstance(name)
            action?.putValue(Action.NAME, name)
            if (ic != null) {
                val icon: Icon? = getSystemResourceIcon(ic)
                //                imageIcons.put(name, icon);
                action?.putValue(Action.SMALL_ICON, icon)
            }
            action?.putValue(Action.SHORT_DESCRIPTION, tooltip)
            if (null != shortcut && shortcut.length > 0) {
                action?.putValue(Action.MNEMONIC_KEY, shortcut[0].toInt())
            }
            action?.putValue(
                Action.ACCELERATOR_KEY,
                getAccelerator(attributes.getValue("accelerator"))
            )
            val cmd = attributes.getValue("actionCommand")
            action?.putValue(Action.ACTION_COMMAND_KEY, cmd)
            val listener = attributes.getValue("listener")
            /*
            if (listener != null) {
                action.addPropertyChangeListener((PropertyChangeListener) Class.forName(listener).newInstance());
            }
*/
            val enabled = attributes.getValue("actionEnabled")
            if (enabled != null) {
                action?.isEnabled = java.lang.Boolean.parseBoolean(enabled)
            }
            menuItem = Class.forName(type).newInstance() as JMenuItem
            menuItem!!.horizontalTextPosition = JMenuItem.RIGHT
            menuItem.action = action
            menuItem.isSelected = "on".equals(attributes.getValue("actionMode"), ignoreCase = true)
            Companion.actions[name] = action!!
            Companion.menuItems[name] = menuItem
            val button = attributes.getValue("toolButton")
            val buttonVisible = attributes.getValue("toolButtonVisible")
            val buttonTextVisible = attributes.getValue("toolButtonTextVisible")
            if ("true".equals(buttonVisible, ignoreCase = true)) {
                val component = Class.forName(button).getDeclaredConstructor().newInstance() as JComponent
                component.toolTipText = tooltip
                if (component is AbstractButton) {
                    val abstractButton = component
                    abstractButton.action = action
                    if (action is ItemListener) {
                        abstractButton.addItemListener(action as ItemListener)
                    }
                    if ("false".equals(buttonTextVisible, ignoreCase = true)) {
                        abstractButton.text = ""
                    }
                }
                toolBar!!.add(component)
                if (component is AbstractButton) {
                    toolButtons[name] = component
                }
            }
            if (action is ItemListener) {
                menuItem.addItemListener(action as ItemListener)
            }
        } catch (e: Exception) {
            logger.severe("Unable to create Menu Item: " + e.message)
            e.printStackTrace() //To change body of catch statement use Options | File Templates.
        }
        return menuItem
    }

    private fun newActionInstance(name: String): Action? {
        var action: Action? = null
        when (name) {
            "New" -> {
                action = NewFontMapAction()
            }
            "Open..." -> {
                action = OpenFontMapAction()
            }
            "ReOpen" -> {
                action = ReOpenAction()
            }
            "Clear List" -> {
                action = ClearReOpenAction()
            }
            "Save" -> {
                action = SaveFontMapAction()
            }
            "Save As..." -> {
                action = SaveAsAction()
            }
            "ReLoad from Disk" -> {
                action = LoadFontMapAction()
            }
            "Close" -> {
                action = CloseAction()
            }
            "Exit" -> {
                action = ExitAction()
            }
            "Undo" -> {
                action = UndoAction()
            }
            "Redo" -> {
                action = RedoAction()
            }
            "Cut" -> {
                action = CutAction()
            }
            "Copy" -> {
                action = CopyAction()
            }
            "Paste" -> {
                action = PasteAction()
            }
            "Select All" -> {
                action = SelectAllAction()
            }
            "Delete" -> {
                action = DeleteAction()
            }
            "Input", "Output" -> {
                action = FileSelectAction()
            }
            "Convert" -> {
                action = TransliterateAction()
            }
            "Stop" -> {
                action = TransliterateStopAction()
            }
            "Reverse Transliterate", "Preserve <Tags>" -> {
                action = ItemListenerAction()
            }
            "Toolbar" -> {
                action = ViewAction.ViewToolBar()
            }
            "Status Bar" -> {
                action = ViewAction.ViewStatus()
            }
            "Mapping Rules" -> {
                action = ViewAction.ViewMapping()
            }
            "Mapping Preview" -> {
                action = ViewAction.ViewSample()
            }
            "Help Topics" -> {
                action = HelpAction()
            }
            "About" -> {
                action = AboutAction()
            }
        }
        return action
    }

    companion object {
        private val actions: MutableMap<String, Action> = HashMap()
        private val toolTips: MutableMap<String, String> = HashMap()
        private val toolButtons: MutableMap<String, AbstractButton> = HashMap()
        private val menuItems: MutableMap<String, JMenuItem?> = HashMap()
        private val menus: MutableMap<String, JMenu> = HashMap()
        private val popupMenus: MutableMap<String, JPopupMenu?> = HashMap()
        private val menuBars: MutableMap<String, JMenuBar?> = HashMap()
        private val toolBars: MutableMap<String, JToolBar?> = HashMap()
        private val stack = Stack<JMenu>()
        private val logger = Logger.getLogger("sted.ui.MenuHandler")

        //        private var lookAndFeelInfos: Array<LookAndFeelInfo>
        @JvmStatic
        var instance: MenuHandler? = null
            get() {
                if (null == field) {
                    try {
                        field = MenuHandler()
                        field!!.loadMenu(getResource("config.menu"))
                    } catch (e: Exception) {
                        logger.throwing("sted.STEDGUI", "main", e)
                    }
                }
                return field
            }
            private set

        @JvmStatic
        fun getToolTips(): Map<String, String> {
            return toolTips
        }

        fun getToolButtons(): Map<String, AbstractButton> {
            return toolButtons
        }

        fun getMenus(): Map<String, JMenu> {
            return menus
        }

        fun getPopupMenus(): Map<String, JPopupMenu?> {
            return popupMenus
        }

        fun getMenuBars(): Map<String, JMenuBar?> {
            return menuBars
        }

        fun getToolBars(): Map<String, JToolBar?> {
            return toolBars
        }

        private fun getAccelerator(key: String?): KeyStroke? {
            return if (key != null && key.length > 0) {
                KeyStroke.getKeyStroke(key)
            } else null
        }

        @JvmStatic
        fun clearReOpenItems(menuHandler: MenuHandler) {
            val menu = menuHandler.getMenu(Resources.ACTION_FILE_REOPEN_COMMAND)
            val sz = menu!!.menuComponentCount
            var i = sz - 2
            while (i > 0) {
                val menuItem = menu.getMenuComponent(0)
                menu.remove(0)
                menuHandler.removeMenuItem(menuItem.name)
                i--
            }
            menu.isEnabled = false
        }

        @JvmStatic
        fun addSampleFontMapMenuItem(
            menu: JMenu,
            fileName: String?
        ) {
            addReOpenItem(menu, fileName, OpenSampleFontMapAction(), false)
        }

        @JvmOverloads
        @JvmStatic
        fun addReOpenItem(
            menu: JMenu,
            fileName: String?, action: Action = ReOpenFontMapAction(), checkInCache: Boolean = true
        ) {
            val menuHandler = instance
            require(!fileName.isNullOrBlank()) { "Invalid File name: $fileName" }
            var menuItem = menuHandler!!.getMenuItem(fileName)
            // check if the menu item already exists.. if not add new
            // this check is done only if cachecheck is enabled.. opensamplefontmap does not require this
            if (!checkInCache || menuItem == null) {
                menuItem = JMenuItem(fileName)
                action.putValue(Action.NAME, fileName)
                action.putValue(
                    Action.ACTION_COMMAND_KEY,
                    Resources.ACTION_FILE_REOPEN_COMMAND
                )
                menuItem.name = fileName
                menuItem.action = action
                menuHandler.addMenuItem(menuItem)
                // always insert as the first item
                menu.insert(menuItem, 0)
                menu.isEnabled = true
            }
        }

        @JvmStatic
        fun disableMenuItem(menuHandler: MenuHandler, fileName: String) {
            disableMenuItem(
                menuHandler.getMenu(Resources.ACTION_FILE_REOPEN_COMMAND),
                fileName
            )
        }

        private fun disableMenuItem(menu: JMenu?, name: String) {
            var count = menu!!.itemCount
            var i = 0
            while (count > Resources.DEFAULT_MENU_COUNT) {
                val menuItem = menu.getItem(i++)
                menuItem.isEnabled = name != menuItem.name
                count--
            }
            menu.isEnabled = menu.itemCount > Resources.DEFAULT_MENU_COUNT
        }

        @JvmStatic
        fun enableReOpenItems(menuHandler: MenuHandler) {
            enableReOpenItems(
                menuHandler.getMenu(Resources.ACTION_FILE_REOPEN_COMMAND)
            )
        }

        @JvmStatic
        fun enableReOpenItems(menu: JMenu?) {
            var count = menu!!.itemCount
            var i = 0
            while (count > Resources.DEFAULT_MENU_COUNT) {
                val menuItem = menu.getItem(i++)
                menuItem.isEnabled = true
                count--
            }
            menu.isEnabled = menu.itemCount > Resources.DEFAULT_MENU_COUNT
        }

        @JvmStatic
        fun enableItemsInReOpenMenu(
            menuHandler: MenuHandler,
            fontMap: FontMap
        ) {
            val menu = menuHandler.getMenu(Resources.ACTION_FILE_REOPEN_COMMAND)
            if (fontMap.isNew) {
                enableReOpenItems(menu)
            } else {
                disableMenuItem(menu, fontMap.fileName)
            }
        }

        @JvmStatic
        val userOptions: String
            get() {
                val menuItems = instance!!.menuItems
                val keys = menuItems.keys.iterator()
                val userOptions = StringBuilder()
                while (keys.hasNext()) {
                    val name = keys.next()
                    val menuItem = menuItems[name]
                    val action = menuItem!!.action
                    if (action is ItemListenerAction) {
                        userOptions.append(name)
                        userOptions.append(Resources.SYMBOL_ASTERISK)
                        userOptions.append(menuItem.isSelected)
                        userOptions.append(Resources.NEWLINE_DELIMITER)
                    } else if (action is ReOpenFontMapAction) {
                        userOptions.append(Resources.ACTION_FILE_REOPEN_COMMAND + name.hashCode())
                        userOptions.append(Resources.SYMBOL_ASTERISK)
                        userOptions.append(name)
                        userOptions.append(Resources.NEWLINE_DELIMITER)
                    }
                }
                return userOptions.toString()
            }

        @JvmStatic
        fun loadLookAndFeelMenu(stedWindow: STEDWindow?) {
            val menuHandler = instance
            val lookAndFeelInfos = UIManager.getInstalledLookAndFeels()
            val buttonGroup = ButtonGroup()
            val curLookAndFeel = UIManager.getLookAndFeel()
            for (lookAndFeelInfo in lookAndFeelInfos) {
                val menuItem = JRadioButtonMenuItem()
                val lafAction = LAFAction()
                lafAction.sTEDWindow = stedWindow
                lafAction.putValue(Action.NAME, lookAndFeelInfo.name)
                lafAction.putValue(
                    Action.ACTION_COMMAND_KEY,
                    lookAndFeelInfo.className
                )
                menuItem.name = lookAndFeelInfo.name
                menuItem.action = lafAction
                menuHandler!!.addMenuItem(menuItem)
                if (menuItem.name == curLookAndFeel.name) {
                    menuItem.isSelected = true
                }
                buttonGroup.add(menuItem)
                val menu = menuHandler.getMenu(Resources.ACTION_VIEW_LAF)
                menu!!.add(menuItem)
            }
        }

    }
}