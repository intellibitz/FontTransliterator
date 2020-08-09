package sted.fontmap

import sted.event.FontListChangeEvent
import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.io.FileHelper.alertAndOpenFont
import sted.io.FileHelper.getInputStream
import sted.io.FileHelper.openFont
import sted.io.Resources
import sted.io.Resources.fonts
import sted.io.Resources.getFont
import java.awt.Font
import java.awt.FontFormatException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.logging.Logger
import javax.swing.event.ChangeListener
import javax.swing.event.EventListenerList

class FontMap() {
    val entries = FontMapEntries()
    private val changeListeners = EventListenerList()
    private val fontListChangeListeners = EventListenerList()
    private val undoListeners = EventListenerList()
    private val redoListeners = EventListenerList()
    var font1: Font = arial
        private set
    var font2: Font = arial
        private set
    var font1Path = "SYSTEM"
    var font2Path = "SYSTEM"
    private var changeEvent: FontMapChangeEvent? = null
    private var fontListChangeEvent: FontListChangeEvent? = null
    private var console = false

    constructor(file: File) : this() {
        fontMapFile = file
    }

    constructor(file: File, isConsole: Boolean) : this(file) {
        setConsole(isConsole)
    }

    var isDirty: Boolean = false
        set(value) {
            setDirtyValue(value)
        }

    private fun setDirtyValue(dirtyVal: Boolean) {
        this.isDirty = dirtyVal
        fireFontMapEditEvent()
    }

    val isReloadable: Boolean
        get() = isDirty && !isNew

    var fontMapFile: File = File("")
        set(value) {
            setFontMapFileValue(value)
        }

    val isFileWritable: Boolean
        get() = fontMapFile.canWrite()

    private fun setFontMapFileValue(file: File) {
        fontMapFile = if (!file.name.toLowerCase().endsWith("xml")) {
            File(file.absolutePath + ".xml")
        } else {
            file
        }
        /*
        if (!selectedFile.exists()){
            try {
                selectedFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logger.throwing("sted.util.FontMapHelper", "saveAsAction", e);
                result = JFileChooser.ERROR_OPTION;
            }
        }
        if (!this.fontMapFile.exists()) {
            throw new IllegalArgumentException("File Does not Exist: " + file.getAbsolutePath());
        }
*/
    }

    fun setConsole(console: Boolean) {
        this.console = console
    }

    fun clear() {
        entries.clear()
        isDirty = false
//        sets dummy file
        fontMapFile = File("")
        font1 = arial
        font2 = arial
        font1Path = Resources.SYSTEM
        font2Path = Resources.SYSTEM
        changeEvent = null
        fontListChangeEvent = null
    }

    private val font1Name: String
        get() = font1.name
    private val font2Name: String
        get() = font2.name
    val fileName: String
        get() = fontMapFile.absolutePath
    val isNew: Boolean
        get() = Resources.EMPTY_STRING == fileName

    /**
     * if the font is a system font.. do not create if not, check for font
     * location if font location is found, readFontMap font.. else prompt for
     * font location finally once font created, readFontMap font
     *
     * @param fontName
     */
    fun setFont1(fontName: String) {
        // if running from console, no need to set fonts
        if (console) {
            return
        }
        val font1FilePath = font1Path
        // check if already set.. this might happen during save
        if (fontName != font1Name) {
            val fontInfo = getFont(fontName)
            if (null != fontInfo) {
                font1 = fontInfo.font
            }
            // if its not a system font, then find it from users path
/*
            if (!Resources.SYSTEM.equals(font1FilePath))
            {
                // if it not absolute path, then its the sample resource
                if (!font1FilePath.contains(File.separator)
                        ||
                        font1FilePath.contains(Resources.getResourceDirPath())
                        )
                {
                    font1FilePath = Resources.prefixResourcePath(font1FilePath);
                }
            }
*/
            // if the font is not yet loaded, then find it from users path
            var file = File(font1FilePath)
            val file2: File?
            if (!file.canRead()) {
                // one last chance..  prompt again for the correct fontfile location
                file2 = alertAndOpenFont(
                    fontName + " Not found in "
                            + font1FilePath +
                            ". FileDialog to choose font location will be opened now",
                    null
                )
                if (file2 != null && file2.canRead()) {
                    file = file2
                }
            }
            setFont1(file)
        }
        fireFontListChangeEvent(font1, 1)
    }

    fun setFont1(file: File) {
        font1Path = file.path
        var inputStream: InputStream? = null
        try {
            inputStream = getInputStream(file)
        } catch (e: FileNotFoundException) {
            // ignore this.. we will try to readFontMap it again
        }
        try {
            if (inputStream == null) {
                // one last chance to readFontMap the font
                val filePath = openFont(null)
                if (filePath != null) {
                    inputStream = getInputStream(filePath)
                }
            }
            if (inputStream != null) {
                font1 = Font.createFont(Font.TRUETYPE_FONT, inputStream)
                val f = font1.deriveFont(Font.PLAIN, 14f)
                val fontInfo = FontInfo(f!!, font1Path)
                val name = font1.name
                if (!name.isNullOrBlank()) {
                    fonts[name] = fontInfo
                    fireFontListChangeEvent(font1, 1)
                    logger.info("Successfully created Font $font1")
                }
            }
        } catch (e: FontFormatException) {
            logger.severe(
                "Unable to Load Font.. FontFormatException: " +
                        e.message
            )
            logger.throwing(javaClass.name, "setFont1", e)
        } catch (e: IOException) {
            logger.severe(
                font1Path +
                        " Unable to Load Font.. IOException: " + e.message
            )
            logger.throwing(javaClass.name, "setFont1", e)
        }
    }

    fun setFont1(font1: Font) {
        this.font1 = font1
    }

    fun setFont2(fontName: String) {
        // if running from console, no need to set fonts
        if (console) {
            return
        }
        val font2FilePath = font2Path
        // check if already set.. this might happen during save
        if (fontName != font2Name) {
            val fontInfo = getFont(fontName)
            if (null != fontInfo) {
                font2 = fontInfo.font
            }
            /*
            if (!Resources.SYSTEM.equals(font2FilePath))
            {
                // if it not absolute path, then its the sample resource
                if (!font2FilePath.contains(File.separator)
                        ||
                        font2FilePath.contains(Resources.getResourceDirPath())
                        )
                {
                    font2FilePath = Resources.prefixResourcePath(font2FilePath);
                }
            }
*/
            // if the font is not yet loaded, then find it from users path
            var file = File(font2FilePath)
            val file2: File?
            if (!file.canRead()) {
                // one last chance..  prompt again for the correct fontfile location
                file2 = alertAndOpenFont(
                    fontName + " Not found in "
                            + font2FilePath +
                            ". FileDialog to choose font location will be opened now",
                    null
                )
                if (file2 != null && file2.canRead()) {
                    file = file2
                }
            }
            setFont2(file)
        }
        fireFontListChangeEvent(font2, 2)
    }

    fun setFont2(fontFile: File) {
        font2Path = fontFile.path
        var inputStream: InputStream? = null
        try {
            inputStream = getInputStream(fontFile)
        } catch (e: FileNotFoundException) {
            // ignore this.. we will try to readFontMap it again
        }
        try {
            if (inputStream == null) {
                val font = openFont(null)
                if (font != null) {
                    inputStream = getInputStream(font)
                }
            }
            if (inputStream != null) {
                font2 = Font.createFont(Font.TRUETYPE_FONT, inputStream)
                val f = font2.deriveFont(Font.PLAIN, 14f)
                val fontInfo = FontInfo(f!!, font2Path)
                fonts[font2.name!!] = fontInfo
                fireFontListChangeEvent(font2, 2)
                logger.info("Successfully created Font $font2")
            }
        } catch (e: FontFormatException) {
            logger.severe(
                "Unable to Load Font.. FontFormatException: " +
                        e.message
            )
            logger.throwing(javaClass.name, "setFont2", e)
        } catch (e: IOException) {
            logger.severe(
                font2Path +
                        " Unable to Load Font.. IOException: " + e.message
            )
            logger.throwing(javaClass.name, "setFont2", e)
        }
    }

    fun setFont2(font2: Font) {
        this.font2 = font2
    }

    fun addFontMapChangeListener(changeListener: FontMapChangeListener?) {
        changeListeners.add(FontMapChangeListener::class.java, changeListener)
    }

    fun removeFontMapChangeListener(
        changeListener: FontMapChangeListener?
    ) {
        changeListeners.remove(FontMapChangeListener::class.java, changeListener)
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    private fun fireFontMapEditEvent() {
        // Guaranteed to return a non-null array
        val listeners = changeListeners.listenerList
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === FontMapChangeListener::class.java) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = FontMapChangeEvent(this)
                }
                (listeners[i + 1] as FontMapChangeListener)
                    .stateChanged(changeEvent!!)
            }
            i -= 2
        }
    }

    fun addFontListChangeListener(changeListener: ChangeListener) {
        fontListChangeListeners.add(ChangeListener::class.java, changeListener)
    }

    fun removeFontListChangeListener(changeListener: ChangeListener) {
        fontListChangeListeners.remove(ChangeListener::class.java, changeListener)
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    private fun fireFontListChangeEvent(font: Font?, index: Int) {
        // Guaranteed to return a non-null array
        val listeners = fontListChangeListeners.listenerList
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === ChangeListener::class.java) {
                // Lazily create the event:
                if (fontListChangeEvent == null) {
                    fontListChangeEvent = FontListChangeEvent(this)
                }
                fontListChangeEvent!!.fontChanged = font
                fontListChangeEvent!!.fontIndex = index
                (listeners[i + 1] as ChangeListener)
                    .stateChanged(fontListChangeEvent)
            }
            i -= 2
        }
    }

    fun addUndoListener(changeListener: FontMapChangeListener?) {
        undoListeners.add(FontMapChangeListener::class.java, changeListener)
    }

    fun removeUndoListener(changeListener: FontMapChangeListener?) {
        undoListeners.remove(FontMapChangeListener::class.java, changeListener)
    }

    fun fireUndoEvent() {
        // Guaranteed to return a non-null array
        val listeners = undoListeners.listenerList
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === FontMapChangeListener::class.java) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = FontMapChangeEvent(this)
                }
                (listeners[i + 1] as FontMapChangeListener)
                    .stateChanged(changeEvent!!)
            }
            i -= 2
        }
    }

    fun addRedoListener(changeListener: FontMapChangeListener?) {
        redoListeners.add(FontMapChangeListener::class.java, changeListener)
    }

    fun removeRedoListener(changeListener: FontMapChangeListener?) {
        redoListeners.remove(FontMapChangeListener::class.java, changeListener)
    }

    fun fireRedoEvent() {
        // Guaranteed to return a non-null array
        val listeners = redoListeners.listenerList
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === FontMapChangeListener::class.java) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = FontMapChangeEvent(this)
                }
                (listeners[i + 1] as FontMapChangeListener)
                    .stateChanged(changeEvent!!)
            }
            i -= 2
        }
    }

    /**
     * @return String the string representation of this FontMap
     */
    override fun toString(): String {
        val stringBuffer = StringBuffer()
        var f = getFont(font1Name)
        stringBuffer.append(
            font1Name + Resources.SYMBOL_ASTERISK + f!!.path
        )
        stringBuffer.append(Resources.NEWLINE_DELIMITER)
        f = getFont(font2Name)
        stringBuffer.append(
            font2Name + Resources.SYMBOL_ASTERISK + f!!.path
        )
        stringBuffer.append(Resources.NEWLINE_DELIMITER)
        for (o in entries.values()) {
            stringBuffer.append(o.toString())
            stringBuffer.append(Resources.NEWLINE_DELIMITER)
        }
        return stringBuffer.toString()
    }

    companion object {
        private val logger = Logger.getLogger("sted.fontmap.FontMap")
        private val arial: Font = fonts[0]?.font ?: Font.decode("Arial")
    }
}