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
    const val ICON_FILE_OUTPUT = "icon.file.output"
    const val FONT_CHAR_MAXINDEX = "font.char.maxindex"
    const val TITLE_TAB_OUTPUT = "title.tab.output"

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
        get() = getSystemResourceIcon(getSetting("icon.sted"))

    @JvmStatic
    val cleanIcon: ImageIcon?
        get() = getSystemResourceIcon(
            getResource("icon.file.normal.state")
        )

    @JvmStatic
    val dirtyIcon: ImageIcon?
        get() = getSystemResourceIcon(
            getResource("icon.file.edit.state")
        )

    @JvmStatic
    val lockIcon: ImageIcon?
        get() = getSystemResourceIcon(getResource("icon.status.lock"))

    @JvmStatic
    val unLockIcon: ImageIcon?
        get() = getSystemResourceIcon(getResource("icon.status.unlock"))

    @JvmStatic
    val version: String?
        get() = getResource("sted.version")

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
        imageIcons["icon.sted"] = getSystemResourceIcon(getResource("icon.sted"))
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