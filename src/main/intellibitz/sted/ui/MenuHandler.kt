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
import javax.swing.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

object MenuHandler : DefaultHandler() {
    val menuBar: JMenuBar = JMenuBar()
    val menuItems: MutableMap<String, JMenuItem> = HashMap()
    val menus: MutableMap<String, JMenu> = HashMap()
    val toolBar: JToolBar = JToolBar()
    val popupMenu: JPopupMenu = JPopupMenu()

    val actions: MutableMap<String, Action> = HashMap()
    val toolTips: MutableMap<String, String> = HashMap()
    val toolButtons: MutableMap<String, AbstractButton> = HashMap()

    private val stack = Stack<JMenu>()
    private var menuItemLast: Boolean = false
    private var menuItemMenuLast: Boolean = false


    @Throws(SAXException::class, ParserConfigurationException::class, IOException::class)
    internal fun loadMenu(xml: String) {
        menuBar.name = "STED-Menubar"
        popupMenu.name = "Mapping"
        toolBar.name = menuBar.name
        toolBar.orientation = JToolBar.HORIZONTAL
        toolBar.isFloatable = false
        toolBar.isRollover = true
        toolBar.add(Box.createVerticalGlue())

        val saxParserFactory = SAXParserFactory.newDefaultNSInstance()
        saxParserFactory.isValidating = true
        saxParserFactory.isNamespaceAware = true
        val saxParser = saxParserFactory.newSAXParser()
        saxParser.parse(ClassLoader.getSystemResourceAsStream(xml), this)

    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (qName) {
            "menuitem" -> {
                if ("::ROOT" == attributes.getValue("name")) return
                val menuItem = createMenuItem(attributes)
                if (menuItem is JMenu) {
                    stack.push(menuItem)
//                    println("pushed ${menuItem.name}")
                } else {
                    val menu = stack.peek()
                    menu.add(menuItem)
                    menuItems[menuItem.name] = menuItem
//                    println("added to menu ${menu.name} : ${menuItem.name}")
                    if (attributes.getValue("separator").toBoolean()) menu.addSeparator()
                    if (attributes.getValue("popup").toBoolean())
                        popupMenu.add(menuItem)
                    if (attributes.getValue("popupSep").toBoolean()) {
                        popupMenu.add(menuItem)
                        popupMenu.addSeparator()
                    }
                }
                menuItemLast = attributes.getValue("last").toBoolean()
                menuItemMenuLast = attributes.getValue("lastMenu").toBoolean()
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        when (qName) {
            "menuitem" -> {
                if (menuItemMenuLast) {
                    endMenu()
                    menuItemMenuLast = false
                } else {
                    if (menuItemLast) {
                        endMenu()
                        menuItemLast = false
                    }
                }
            }
        }
    }

    private fun endMenu() {
        if (stack.isEmpty()) return
        val menu = stack.pop()
//        println("pop ${menu.name}")
        if (stack.isEmpty()) {
            toolBar.add(Box.createHorizontalStrut(5))
            menuBar.add(menu)
//            println("added to menubar ${menu.name}")
        } else {
            val parent = stack.peek()
            parent.add(menu)
//            println("added to parent ${parent.name}: ${menu.name}")
        }
        menus[menu.name] = menu
    }

    private fun createMenuItem(attributes: Attributes): JMenuItem {
        val type = attributes.getValue("type")
        val menuItem: JMenuItem = Class.forName(type).getDeclaredConstructor().newInstance() as JMenuItem
        val name = attributes.getValue("name")
        menuItem.name = name
        menuItem.text = name
        menuItem.horizontalTextPosition = JMenuItem.RIGHT
        val enabled = attributes.getValue("actionEnabled").toBoolean()
        menuItem.isEnabled = enabled
        menuItem.isSelected = attributes.getValue("actionMode").toBoolean()

        val tooltip = attributes.getValue("tooltip")
        toolTips[name] = tooltip

        val action = newActionInstance(name)
        if (action != null) {
            menuItem.action = action
            if (action is ItemListener) {
                menuItem.addItemListener(action as ItemListener)
            }
            actions[name] = action
            action.putValue(Action.NAME, name)
            action.putValue(Action.ACTION_COMMAND_KEY, attributes.getValue("actionCommand"))
            action.putValue(Action.SHORT_DESCRIPTION, tooltip)
            action.isEnabled = enabled
            val ic = attributes.getValue("icon")
            if (ic != null) {
                action.putValue(Action.SMALL_ICON, getResourceIcon(ic))
                //                imageIcons.put(name, icon);
            }
            action.putValue(
                Action.ACCELERATOR_KEY,
                getAccelerator(attributes.getValue("accelerator"))
            )
            val shortcut = attributes.getValue("mnemonic")
            if (!shortcut.isNullOrEmpty()) {
                val mnemonic = shortcut[0].toInt()
                menuItem.mnemonic = mnemonic
                action.putValue(Action.MNEMONIC_KEY, mnemonic)
            }
        }
//            val listener = attributes.getValue("listener")
        /*
        if (listener != null) {
            action.addPropertyChangeListener((PropertyChangeListener) Class.forName(listener).newInstance());
        }
*/
//        val buttonVisible = attributes.getValue("toolButtonVisible").toBoolean()
        val button = attributes.getValue("toolButton")
        if (!button.isNullOrEmpty()) {
            val component = Class.forName(button).getDeclaredConstructor().newInstance() as JComponent
            if (component is AbstractButton) {
                component.toolTipText = tooltip
                toolButtons[name] = component
                toolBar.add(component)
                component.action = action
                if (action is ItemListener) {
                    component.addItemListener(action as ItemListener)
                }
                if (!attributes.getValue("toolButtonTextVisible").toBoolean()) {
                    component.text = ""
                }
            }
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

/*
    @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
    private fun createMenu(attributes: Attributes): JMenu {
        val menu = JMenu()
        val name = attributes.getValue("name")
        menu.name = name
        menu.text = name
        val mnemonic = attributes.getValue("mnemonic")
        menu.setMnemonic(mnemonic[0])
        val action = newActionInstance(name)
        action?.putValue(Action.NAME, name)
        action?.putValue(Action.MNEMONIC_KEY, mnemonic[0].toInt())
        menu.action = action
        actions[name] = action!!
        menu.isEnabled = attributes.getValue("actionEnabled").toBoolean()
        return menu
    }
*/

/*
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
*/

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
            menuItem?.isEnabled = name != menuItem?.name
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
            menuItem?.isEnabled = true
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