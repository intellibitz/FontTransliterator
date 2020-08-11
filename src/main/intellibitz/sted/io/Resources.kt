package sted.io

import org.xml.sax.SAXException
import sted.fontmap.FontInfo
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File
import java.io.IOException
import java.util.*
import java.util.logging.Logger
import javax.swing.ImageIcon
import javax.xml.parsers.ParserConfigurationException

object Resources {
    const val COLON = ":"
    const val EMPTY_STRING = ""
    const val SPACE = " "
    const val EQUALS = "="
    const val SYMBOL_ASTERISK = "*"
    const val NEWLINE_DELIMITER = "\n"
    private const val STED_VERSION = "sted.version"
    const val FONTMAP_FILE = "fontmap.file"
    const val LABEL_ADD = "label.add"
    const val LABEL_CLEAR = "label.clear"
    const val LABEL_FONT_LOAD = "label.font.load"
    const val ICON_FILE_INPUT = "icon.file.input"
    const val ICON_FILE_OUTPUT = "icon.file.output"
    private const val ICON_FILE_NORMAL_STATE = "icon.file.normal.state"
    private const val ICON_FILE_EDIT_STATE = "icon.file.edit.state"
    private const val ICON_STED = "icon.sted"
    const val ICON_HELP = "icon.help"
    const val ICON_HELP_HOME = "icon.help.home"
    const val ICON_HELP_BACK = "icon.help.back"
    const val ICON_HELP_FORWARD = "icon.help.forward"
    private const val ICON_LOCK = "icon.status.lock"
    private const val ICON_UNLOCK = "icon.status.unlock"
    const val TITLE_ABOUT_STED = "title.about.sted"
    const val TITLE_KEYPAD = "title.keypad"
    const val TITLE_MAPPING = "title.mapping"
    const val TITLE_MAPPING_RULE = "title.mapping.rule"
    const val TITLE_MAPPING_PREVIEW = "title.mapping.preview"
    const val KEYPAD_COLUMN_COUNT = "keypad.column.count"
    const val FONT_CHAR_MAXINDEX = "font.char.maxindex"
    const val TITLE_TAB_INPUT = "title.tab.input"
    const val TITLE_TAB_OUTPUT = "title.tab.output"
    const val TITLE_TAB_FONTMAP = "title.tab.fontmap"
    const val TIP_TAB_FONTMAP = "tip.tab.fontmap"
    const val TITLE_TABLE_COLUMN_SYMBOL1 = "title.table.column.symbol1"
    const val TITLE_TABLE_COLUMN_SYMBOL2 = "title.table.column.symbol2"
    const val TITLE_TABLE_COLUMN_EQUALS = "title.table.column.equals"
    const val TITLE_TABLE_COLUMN_FIRST_LETTER = "title.table.column.letter1"
    const val TITLE_TABLE_COLUMN_LAST_LETTER = "title.table.column.letter2"
    const val TITLE_TABLE_COLUMN_FOLLOWED_BY = "title.table.column.followed"
    const val TITLE_TABLE_COLUMN_PRECEDED_BY = "title.table.column.preceded"
    const val TITLE_HELP = "title.help"
    const val HELP_INDEX = "help.index"
    const val STED_HOME_PATH = "sted.home.path"
    const val SAMPLE_INPUT_TEXT = "sample.input.text"
    const val ACTION_CONVERT_NAME = "Convert"
    const val ACTION_STOP_NAME = "Stop"
    const val ACTION_PRESERVE_TAGS = "Preserve <Tags>"
    const val ACTION_TRANSLITERATE_REVERSE = "Reverse Transliterate"
    const val ACTION_FILE_RELOAD = "ReLoad from Disk"
    const val ACTION_VIEW_LAF = "Look & Feel"
    const val ACTION_FILE_NEW_COMMAND = "New"
    const val ACTION_FILE_RELOAD_COMMAND = "ReLoad"
    const val ACTION_FILE_REOPEN_COMMAND = "ReOpen"
    const val ACTION_FILE_SAVE_COMMAND = "Save"
    const val ACTION_FILE_SAVEAS_COMMAND = "Save As..."
    const val ACTION_FILE_CLOSE_COMMAND = "Close"
    const val ACTION_SELECT_INPUT_FILE_COMMAND = "Input"
    const val ACTION_SELECT_OUTPUT_FILE_COMMAND = "Output"
    const val ACTION_DELETE_COMMAND = "Delete"
    const val ACTION_CUT_COMMAND = "Cut"
    const val ACTION_COPY_COMMAND = "Copy"
    const val ACTION_UNDO_COMMAND = "Undo"
    const val ACTION_REDO_COMMAND = "Redo"
    const val ACTION_PASTE_COMMAND = "Paste"
    const val ACTION_SELECT_ALL_COMMAND = "Select All"
    const val ACTION_VIEW_SAMPLE = "Mapping Preview"
    const val ACTION_VIEW_MAPPING = "Mapping Rules"
    const val MENU_POPUP_MAPPING = "Mapping"
    const val FOLLOWED_BY = "Followed By: "
    const val PRECEDED_BY = "Preceded By: "
    const val XML = "xml"
    const val ENTRIES = "entries"
    const val ENTRY_CONDITIONAL_AND = "AND"
    const val ENTRY_CONDITIONAL_OR = "OR"
    const val ENTRY_CONDITIONAL_NOT = "NOT"
    const val ENTRY_TOSTRING_DELIMITER = ":"
    const val HTML_TAG_START = "<"
    const val HTML_TAG_START_ESCAPE = "&"
    const val HTML_TAG_END = ">"
    const val HTML_TAG_END_ESCAPE = ";"
    const val RULE_TITLE = " If <> is: "
    const val ENTRY_STATUS_ADD = 1
    const val ENTRY_STATUS_EDIT = 2
    const val ENTRY_STATUS_DELETE = 3

    // @since version 0.61
    const val MENUBAR_STED = "STED-MenuBar"

    // @since version 0.62
    const val INPUT_FILE = "input.file"
    const val OUTPUT_FILE = "output.file"
    const val ICON_GC2 = "icon.gc.rollover"
    const val DEFAULT_MENU_COUNT = 2
    const val MENU_SAMPLES_NAME = "Samples"

    @JvmField
    val imageIcons: MutableMap<String, ImageIcon?> = HashMap()
    private val settings: MutableMap<String, String> = HashMap()
    private val logger = Logger.getLogger("sted.io.Resources")
    private var resourceBundle: ResourceBundle? = null
    var id = 0
        get() = ++field
        private set

    @JvmStatic
    val sTEDIcon: ImageIcon?
        get() = getSystemResourceIcon(getSetting(ICON_STED))

    @JvmStatic
    val cleanIcon: ImageIcon?
        get() = getSystemResourceIcon(
            getResource(ICON_FILE_NORMAL_STATE)
        )

    @JvmStatic
    val dirtyIcon: ImageIcon?
        get() = getSystemResourceIcon(
            getResource(ICON_FILE_EDIT_STATE)
        )

    @JvmStatic
    val lockIcon: ImageIcon?
        get() = getSystemResourceIcon(getResource(ICON_LOCK))

    @JvmStatic
    val unLockIcon: ImageIcon?
        get() = getSystemResourceIcon(getResource(ICON_UNLOCK))

    @JvmStatic
    val version: String?
        get() = getResource(STED_VERSION)

    @JvmStatic
    fun getResource(name: String): String? {
        var value: String? = null
        try {
            value = resourceBundle?.getString(name)
        } catch (e: MissingResourceException) {
            // ignore, since we will try alternates
        }
        // if not from resource bundle.. try system property.. or the settings file
        if (value == null) {
            value = getPropertySettings(name)
        }
        if (value == null) {
            logger.severe("Resource not found for: $name")
        }
        return value
    }

    private fun getPropertySettings(name: String): String? {
        // try the system property - might come from command line
        // system property will override the options in the settings file
        var value = System.getProperty(name)
        if (value == null) {
            // read from the settings file
            value = getSetting(name)
        }
        return value
    }

    @JvmStatic
    fun getSetting(name: String): String? {
        return settings[name]
    }

    @JvmStatic
    fun getSettingBeginsWith(prefix: String?): ArrayList<String?> {
        val keys: Iterator<String> = settings.keys.iterator()
        val result = ArrayList<String?>()
        while (keys.hasNext()) {
            val key = keys.next()
            if (key.startsWith(prefix!!)) {
                val `val` = settings[key]
                result.add(`val`)
            }
        }
        return result
    }

    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    private fun readSettings(path: String) {
        val settingsXMLHandler = SettingsXMLHandler()
        settingsXMLHandler.init(File(path))
        settingsXMLHandler.read()
        settings.putAll(settingsXMLHandler.settings)
    }

    @JvmStatic
    fun getSystemResourceIcon(iconName: String?): ImageIcon? {
        if (iconName != null) {
            try {
                var imageIcon = imageIcons[iconName]
                if (null != imageIcon) {
                    return imageIcon
                }
                val url = ClassLoader.getSystemResource(iconName)
                if (url == null) {
                    // try with the filename
                    imageIcon = ImageIcon(iconName)
                    imageIcons[iconName] = imageIcon
                } else {
                    imageIcon = ImageIcon(url)
                    imageIcons[iconName] = imageIcon
                }
                return imageIcon
            } catch (e: NullPointerException) {
                logger.warning(
                    "No icon found or can be loaded for " + iconName
                            + " " + e.message +
                            Arrays.toString(e.stackTrace)
                )
            }
        }
        logger.warning(
            "Cannot find Resource. Returning null as icon for $iconName"
        )
        return null
    }

/*
    val sampleFontMap: String
        get() = prefixResourcePath(getResource(SAMPLE_FONTMAP))
    fun prefixResourcePath(path: String?): String {
        return suffixFileSeparator(RESOURCE_PATH_VAL!!) +
                path
    }
*/


    @JvmStatic
    val fonts: MutableMap<String, FontInfo> = HashMap()
/*
        @JvmStatic
        fun getFonts(): Map<String, FontInfo> {
            return fonts
        }
*/

    @JvmStatic
    fun getFont(fontName: String): FontInfo? {
        return fonts[fontName]
    }

    fun loadFonts(allFonts: Array<Font>) {
        for (font in allFonts) {
            val f = font.deriveFont(Font.PLAIN, 14f)
            fonts[font.name] = FontInfo(f, "SYSTEM")
        }
    }


    init {
        resourceBundle = ResourceBundle.getBundle("config.sted", Locale.getDefault())
        logger.info("retrieved resource bundle $resourceBundle")
        var resource = getResource("settings.sted.ui")
        if (!resource.isNullOrEmpty()) {
            readSettings(resource)
        }
        resource = getResource("settings.sted.user")
        if (!resource.isNullOrEmpty()) {
            readSettings(resource)
        }
        imageIcons[ICON_STED] = getSystemResourceIcon(getResource(ICON_STED))
        // load all system fonts
        loadFonts(GraphicsEnvironment.getLocalGraphicsEnvironment().allFonts)
    }

/*
    @Throws(Throwable::class)
    protected fun finalize() {
        resourceBundle = null
    }
*/
}