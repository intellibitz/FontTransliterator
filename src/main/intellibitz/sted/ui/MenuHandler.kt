package sted.ui

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import sted.actions.*
import sted.fontmap.FontMap
import sted.io.Resources.getResourceIcon
import java.awt.event.ItemListener
import java.io.IOException
import java.util.*
import java.util.logging.Logger
import javax.swing.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

object MenuHandler : DefaultHandler() {
    private val menuBar: JMenuBar = JMenuBar()
    private val toolBar: JToolBar = JToolBar()
    private val popupMenu: JPopupMenu = JPopupMenu()
    private var isPopup = false

    val actions: MutableMap<String, Action> = HashMap()
    val toolTips: MutableMap<String, String> = HashMap()
    val toolButtons: MutableMap<String, AbstractButton> = HashMap()
    val menuItems: MutableMap<String, JMenuItem> = HashMap()
    val menus: MutableMap<String, JMenu> = HashMap()
    val popupMenus: MutableMap<String, JPopupMenu> = HashMap()
    val menuBars: MutableMap<String, JMenuBar> = HashMap()
    val toolBars: MutableMap<String, JToolBar> = HashMap()
    private val stack = Stack<JMenu>()


    @Throws(SAXException::class, ParserConfigurationException::class, IOException::class)
    internal fun loadMenu(xml: String) {
        val saxParserFactory = SAXParserFactory.newDefaultNSInstance()
        saxParserFactory.isValidating = true
        saxParserFactory.isNamespaceAware = true
        val saxParser = saxParserFactory.newSAXParser()
        saxParser.parse(ClassLoader.getSystemResourceAsStream(xml), this)
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if ("menubar" == qName) {
            menuBar.name = attributes.getValue("name")
            toolBar.name = attributes.getValue("toolBarName")
            toolBar.orientation = JToolBar.HORIZONTAL
            toolBar.isFloatable = false
            toolBar.isRollover = true
        } else if ("menupopup" == qName) {
            isPopup = true
            popupMenu.name = attributes.getValue("name")
        } else if ("menu" == qName) {
            isPopup = false
            createMenu(attributes)
        } else if ("menuitem" == qName) {
            if (isPopup) {
                createMenuItem(attributes, popupMenu)
            } else {
                createMenuItem(attributes, stack.peek())
            }
        } else if ("menuitemref" == qName) {
            if (isPopup) {
                createMenuItemRef(attributes, popupMenu)
            } else {
                createMenuItemRef(attributes, stack.peek())
            }
        } else if ("seperator" == qName) {
            if (isPopup) {
                popupMenu.addSeparator()
            } else {
                val menu = stack.peek()
                menu.addSeparator()
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        when (qName) {
            "menubar" -> {
                menuBars[menuBar.name] = menuBar
                toolBar.add(Box.createVerticalGlue())
                toolBars[toolBar.name] = toolBar
            }
            "menu" -> {
                val menu = stack.pop()
                if (stack.isEmpty()) {
                    toolBar.add(Box.createHorizontalStrut(5))
                    menuBar.add(menu)
                } else {
                    val parent = stack.peek()
                    parent.add(menu)
                }
                menus[menu.name] = menu
            }
            "menuitem" -> {
            }
            "menupopup" -> {
                popupMenus[popupMenu.name] = popupMenu
            }
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
            actions[name] = action!!
        }
        menu.isEnabled = java.lang.Boolean.parseBoolean(attributes.getValue("actionEnabled"))
        stack.push(menu)
        return menu
    }

    private fun createMenuItemRef(attributes: Attributes): JMenuItem {
        val menuItem = menuItems[attributes.getValue("name")]
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

    private fun createMenuItemRef(attributes: Attributes, menu: JMenu) {
        val name = attributes.getValue("name")
        if ("::separator" == name) {
            menu.addSeparator()
        } else {
            menu.add(createMenuItemRef(attributes))
        }
    }

    private fun createMenuItemRef(attributes: Attributes, menu: JPopupMenu) {
        val name = attributes.getValue("name")
        if ("::separator" == name) {
            menu.addSeparator()
        } else {
            menu.add(createMenuItemRef(attributes))
        }
    }

    private fun createMenuItem(attributes: Attributes, menu: JMenu) {
        val name = attributes.getValue("name")
        if ("::separator" == name) {
            menu.addSeparator()
        } else {
            menu.add(createMenuItem(attributes))
        }
    }

    private fun createMenuItem(attributes: Attributes, menu: JPopupMenu) {
        val name = attributes.getValue("name")
        if ("::separator" == name) {
            menu.addSeparator()
        } else {
            menu.add(createMenuItem(attributes))
        }
    }

    private fun createMenuItem(attributes: Attributes): JMenuItem {
        val name = attributes.getValue("name")
        val type = attributes.getValue("type")
        val ic = attributes.getValue("icon")
        val tooltip = attributes.getValue("tooltip")
        val shortcut = attributes.getValue("mnemonic")
        toolTips[name] = tooltip
//            val value = attributes.getValue("action")
        val action = newActionInstance(name)
        action?.putValue(Action.NAME, name)
        if (ic != null) {
            val icon: Icon? = getResourceIcon(ic)
            //                imageIcons.put(name, icon);
            action?.putValue(Action.SMALL_ICON, icon)
        }
        action?.putValue(Action.SHORT_DESCRIPTION, tooltip)
        if (null != shortcut && shortcut.isNotEmpty()) {
            action?.putValue(Action.MNEMONIC_KEY, shortcut[0].toInt())
        }
        action?.putValue(
            Action.ACCELERATOR_KEY,
            getAccelerator(attributes.getValue("accelerator"))
        )
        val cmd = attributes.getValue("actionCommand")
        action?.putValue(Action.ACTION_COMMAND_KEY, cmd)
//            val listener = attributes.getValue("listener")
        /*
        if (listener != null) {
            action.addPropertyChangeListener((PropertyChangeListener) Class.forName(listener).newInstance());
        }
*/
        val enabled = attributes.getValue("actionEnabled")
        if (enabled != null) {
            action?.isEnabled = java.lang.Boolean.parseBoolean(enabled)
        }
        val menuItem: JMenuItem = Class.forName(type).getDeclaredConstructor().newInstance() as JMenuItem
        menuItem.horizontalTextPosition = JMenuItem.RIGHT
        menuItem.action = action
        menuItem.isSelected = "on".equals(attributes.getValue("actionMode"), ignoreCase = true)
        actions[name] = action!!
// sub menu can be coming in as menuitem from xml, or json
        if (menuItem is JMenu) {
            menus[name] = menuItem
//                stack.push(menuItem)
        } else {
            menuItems[name] = menuItem
        }
        val button = attributes.getValue("toolButton")
        val buttonVisible = attributes.getValue("toolButtonVisible")
        val buttonTextVisible = attributes.getValue("toolButtonTextVisible")
        if ("true".equals(buttonVisible, ignoreCase = true)) {
            val component = Class.forName(button).getDeclaredConstructor().newInstance() as JComponent
            component.toolTipText = tooltip
            if (component is AbstractButton) {
                component.action = action
                if (action is ItemListener) {
                    component.addItemListener(action as ItemListener)
                }
                if ("false".equals(buttonTextVisible, ignoreCase = true)) {
                    component.text = ""
                }
            }
            toolBar.add(component)
            if (component is AbstractButton) {
                toolButtons[name] = component
            }
        }
        if (action is ItemListener) {
            menuItem.addItemListener(action as ItemListener)
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

    private fun getAccelerator(key: String): KeyStroke? {
        return if (key.isNotEmpty()) {
            KeyStroke.getKeyStroke(key)
        } else null
    }

    fun clearReOpenItems() {
        val menu = menus["ReOpen"]
        val sz = menu!!.menuComponentCount
        var i = sz - 2
        while (i > 0) {
            val menuItem = menu.getMenuComponent(0)
            menu.remove(0)
            menuItems.remove(menuItem.name)
            i--
        }
        menu.isEnabled = false
    }

    fun addSampleFontMapMenuItem(
        menu: JMenu,
        fileName: String
    ) {
        addReOpenItem(menu, fileName, OpenSampleFontMapAction(), false)
    }

    fun addReOpenItem(
        menu: JMenu,
        fileName: String,
        action: Action = ReOpenFontMapAction(),
        checkInCache: Boolean = true
    ) {
        require(!fileName.isBlank()) { "Invalid File name: $fileName" }
        var menuItem = menuItems[fileName]
        // check if the menu item already exists.. if not add new
        // this check is done only if cachecheck is enabled.. opensamplefontmap does not require this
        if (!checkInCache || menuItem == null) {
            menuItem = JMenuItem(fileName)
            action.putValue(Action.NAME, fileName)
            action.putValue(
                Action.ACTION_COMMAND_KEY,
                "ReOpen"
            )
            menuItem.name = fileName
            menuItem.action = action
            menuItems[menuItem.name] = menuItem
            // always insert as the first item
            menu.insert(menuItem, 0)
            menu.isEnabled = true
        }
    }

    fun disableMenuItem(fileName: String) {
        disableMenuItem(menus["ReOpen"]!!, fileName)
    }

    private fun disableMenuItem(menu: JMenu, name: String) {
        var count = menu.itemCount
        var i = 0
        while (count > 2) {
            val menuItem = menu.getItem(i++)
            menuItem.isEnabled = name != menuItem.name
            count--
        }
        menu.isEnabled = menu.itemCount > 2
    }

    fun enableReOpenItems() {
        enableReOpenItems(menus["ReOpen"]!!)
    }

    fun enableReOpenItems(menu: JMenu) {
        var count = menu.itemCount
        var i = 0
        while (count > 2) {
            val menuItem = menu.getItem(i++)
            menuItem.isEnabled = true
            count--
        }
        menu.isEnabled = menu.itemCount > 2
    }

    fun enableItemsInReOpenMenu(fontMap: FontMap) {
        val menu = menus["ReOpen"]!!
        if (fontMap.isNew) {
            enableReOpenItems(menu)
        } else {
            disableMenuItem(menu, fontMap.fileName)
        }
    }

    val userOptions: String
        get() {
            val keys = menuItems.keys.iterator()
            val userOptions = StringBuilder()
            while (keys.hasNext()) {
                val name = keys.next()
                val menuItem = menuItems[name]
                val action = menuItem!!.action
                if (action is ItemListenerAction) {
                    userOptions.append(name)
                    userOptions.append("*")
                    userOptions.append(menuItem.isSelected)
                    userOptions.append("\n")
                } else if (action is ReOpenFontMapAction) {
                    userOptions.append("ReOpen" + name.hashCode())
                    userOptions.append("*")
                    userOptions.append(name)
                    userOptions.append("\n")
                }
            }
            return userOptions.toString()
        }

    fun loadLookAndFeelMenu() {
        val lookAndFeelInfos = UIManager.getInstalledLookAndFeels()
        val buttonGroup = ButtonGroup()
        val curLookAndFeel = UIManager.getLookAndFeel()
        for (lookAndFeelInfo in lookAndFeelInfos) {
            val menuItem = JRadioButtonMenuItem()
            val lafAction = LAFAction()
            lafAction.putValue(Action.NAME, lookAndFeelInfo.name)
            lafAction.putValue(
                Action.ACTION_COMMAND_KEY,
                lookAndFeelInfo.className
            )
            menuItem.name = lookAndFeelInfo.name
            menuItem.action = lafAction
            menuItems[menuItem.name] = menuItem
            if (menuItem.name == curLookAndFeel.name) {
                menuItem.isSelected = true
            }
            buttonGroup.add(menuItem)
            val menu = menus["Look & Feel"]
            menu!!.add(menuItem)
        }
    }

}