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
    private val imageIcons: MutableMap<String, ImageIcon> = HashMap()
    val fonts: MutableMap<String, FontInfo> = HashMap()
    private val settings: MutableMap<String, String> = HashMap()
    private val logger = Logger.getLogger("sted.io.Resources")
    private lateinit var resourceBundle: ResourceBundle
    var sTEDIcon: ImageIcon? = null
    var cleanIcon: ImageIcon? = null
    var dirtyIcon: ImageIcon? = null
    var lockIcon: ImageIcon? = null
    var unLockIcon: ImageIcon? = null

    var id = 0
        get() = ++field
        private set
    val version: String?
        get() = getResource("sted.version")


    private fun loadImageIcons() {
        sTEDIcon = getResourceIcon("icon.sted")
        if (sTEDIcon != null)
            imageIcons["icon.sted"] = sTEDIcon!!
        cleanIcon = getResourceIcon("icon.file.normal.state")
        if (cleanIcon != null)
            imageIcons["icon.file.normal.state"] = cleanIcon!!
        dirtyIcon = getResourceIcon("icon.file.edit.state")
        if (dirtyIcon != null)
            imageIcons["icon.file.edit.state"] = dirtyIcon!!
        lockIcon = getResourceIcon("icon.status.lock")
        if (lockIcon != null)
            imageIcons["icon.status.lock"] = lockIcon!!
        unLockIcon = getResourceIcon("icon.status.unlock")
        if (unLockIcon != null)
            imageIcons["icon.status.unlock"] = unLockIcon!!
    }

    fun getResource(name: String): String? {
        var value: String? = null
        try {
            value = resourceBundle.getString(name)
        } catch (e: MissingResourceException) {
            // ignore, since we will try alternates
        }
        // if not from resource bundle.. try system property.. or the settings file
        if (value == null) {
            value = getPropertySettings(name)
        }
        return value
    }

    fun getResourceIcon(iconName: String): ImageIcon? {
        try {
            var resource = getResource(iconName)
            if (resource.isNullOrEmpty())
                resource = iconName
            var imageIcon = imageIcons[resource]
            if (null != imageIcon) {
                return imageIcon
            }
            val url = ClassLoader.getSystemResource(resource)
            if (url == null) {
                // try with the filename
                imageIcon = ImageIcon(resource)
                imageIcons[resource] = imageIcon
            } else {
                imageIcon = ImageIcon(url)
                imageIcons[resource] = imageIcon
            }
            return imageIcon
        } catch (e: NullPointerException) {
            logger.warning(
                "No icon found or can be loaded for " + iconName
                        + " " + e.message +
                        Arrays.toString(e.stackTrace)
            )
        }
        return null
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

    fun getSetting(name: String): String? {
        return settings[name]
    }

    fun getSettingBeginsWith(prefix: String): ArrayList<String?> {
        val keys: Iterator<String> = settings.keys.iterator()
        val result = ArrayList<String?>()
        while (keys.hasNext()) {
            val key = keys.next()
            if (key.startsWith(prefix)) {
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

/*
    val sampleFontMap: String
        get() = prefixResourcePath(getResource(SAMPLE_FONTMAP))
    fun prefixResourcePath(path: String?): String {
        return suffixFileSeparator(RESOURCE_PATH_VAL!!) +
                path
    }
*/


/*
        @JvmStatic
        fun getFonts(): Map<String, FontInfo> {
            return fonts
        }
*/

    fun getFont(fontName: String): FontInfo? {
        return fonts[fontName]
    }

    fun loadFonts(allFonts: Array<Font>) {
        for (font in allFonts) {
            val f = font.deriveFont(Font.PLAIN, 14f)
            fonts[font.name] = FontInfo(f, "SYSTEM")
        }
    }


    fun init() {
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
        loadImageIcons()
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