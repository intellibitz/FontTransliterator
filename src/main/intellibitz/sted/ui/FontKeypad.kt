package sted.ui

import sted.actions.LoadFontAction
import sted.event.*
import sted.fontmap.FontInfo
import sted.fontmap.FontMap
import sted.io.Resources
import sted.io.Resources.fonts
import sted.io.Resources.getFont
import sted.io.Resources.getResource
import sted.io.Resources.getSetting
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.io.File
import java.util.*
import java.util.logging.Logger
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.EventListenerList

/**
 * FontKeypad holds font dropdown and keypad for selecting characters
 */
abstract class FontKeypad protected constructor() : JPanel(), ItemListener, FontMapChangeListener, IKeypadEventSource {
    val keys = ArrayList<JButton>()
    val fontSelector = FontList()
    private val keypadListeners = EventListenerList()
    private val keypad = JPanel()
    var fontMap: FontMap? = null
        private set
    var currentFont: Font? = null
        private set
    private var keyColumns = 6
    private var fontMaxIndex = 65536
    fun init() {
        val titledBorder = TitledBorder(getResource(Resources.TITLE_KEYPAD))
        titledBorder.titleJustification = TitledBorder.CENTER
        border = titledBorder
        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 0.0
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        val loadFont = JButton(LoadFontAction(this))
        gridBagLayout.setConstraints(loadFont, gridBagConstraints)
        add(loadFont)
        val fonts: Map<String, FontInfo> = fonts
        val fontsListModel = FontsListModel()
        fontsListModel.setFonts(fonts)
        fontSelector.model = fontsListModel
        setCurrentFont(fontSelector.getItemAt(0) as String)
        fontSelector.selectedItem = currentFont
        fontSelector.addItemListener(this)
        gridBagLayout.setConstraints(fontSelector, gridBagConstraints)
        add(fontSelector)

        //
        gridBagConstraints.weightx = GridBagConstraints.RELATIVE.toDouble()
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        val fontKeypad: JComponent = fontKeypad
        gridBagLayout.setConstraints(fontKeypad, gridBagConstraints)
        //
        add(fontKeypad)
    }

    fun load() {
        val count = getSetting("keypad.column.count")
        if (count != null) keyColumns = count.toInt()
        val max = getSetting("font.char.maxindex")
        if (max != null) fontMaxIndex = max.toInt()
    }

    override fun itemStateChanged(e: ItemEvent) {
        setCurrentFont(e.item.toString())
        resetKeypad()
    }

    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        fontMap = fontMapChangeEvent.fontMap
        setCurrentFont()
        resetKeypad()
    }

    val selectedFont: String?
        get() {
            val selectedItem = fontSelector.selectedItem ?: return null
            return selectedItem.toString()
        }

    //        resetKeypad();
    private val fontKeypad: JScrollPane
        get() {
            keypad.border = BorderFactory.createEmptyBorder()
            //        resetKeypad();
            val jScrollPane = JScrollPane()
            jScrollPane.viewport.add(keypad)
            return jScrollPane
        }

    private fun resetKeypad() {
        keypad.removeAll()
        val gridBagLayout = GridBagLayout()
        keypad.layout = gridBagLayout
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 0.0
        gridBagConstraints.weighty = 0.0
        val numOfGlyphs = currentFont!!.numGlyphs
        var i = 0
        var j = 0
        while (i < fontMaxIndex && j < numOfGlyphs) {
            val c = i.toChar()
            if (currentFont!!.canDisplay(c)) {
                val cmd = Resources.EMPTY_STRING + c
                val keyButton: JButton
                if (keys.isNotEmpty() && j < keys.size) {
                    keyButton = keys[j]
                } else {
                    keyButton = JButton()
                    keys.add(j, keyButton)
                }
                // remove all the action listeners previously added
                // only 1 action listener to be added per button
                val actionListeners = keyButton.actionListeners
                if (actionListeners != null && actionListeners.isNotEmpty()) {
                    for (newVar in actionListeners) {
                        keyButton.removeActionListener(newVar)
                    }
                }
                keyButton.font = currentFont
                keyButton.text = cmd
                gridBagConstraints.gridwidth = 1
                if ((j + 1) % keyColumns == 0) {
                    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
                }
                gridBagLayout.setConstraints(keyButton, gridBagConstraints)
                keypad.add(keyButton)
                j++
            }
            i++
        }
        //        addKeypadListener();
        fireKeypadReset()
        keypad.updateUI()
        // garbage collect
        System.gc()
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    override fun fireKeypadReset() {
        // Guaranteed to return a non-null array
        val listeners = keypadListeners.listenerList
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        val keypadEvent = KeypadEvent(this)
        while (i >= 0) {
            if (listeners[i] === IKeypadListener::class.java) {
                (listeners[i + 1] as IKeypadListener)
                    .keypadReset(keypadEvent)
            }
            i -= 2
        }
    }

    override fun addKeypadListener(keypadListener: IKeypadListener) {
        keypadListeners.add(IKeypadListener::class.java, keypadListener)
    }

    fun removeKeypadListener(keypadListener: IKeypadListener?) {
        keypadListeners.remove(IKeypadListener::class.java, keypadListener)
    }

    open fun setCurrentFont(fontName: String) {
        val font = getFont(fontName)
        if (font != null) setCurrentFont(font.font)
    }

    fun setCurrentFont(font: Font?) {
        if (font == null) {
            fontSelector.selectedIndex = 0
            val item = fontSelector.selectedItem
            if (item != null) {
                val info = getFont(item.toString())
                if (info != null) currentFont = info.font
            }
        } else {
            currentFont = font
            fontSelector.selectedItem = currentFont!!.name
        }
        setFont(currentFont)
    }

    protected abstract fun setCurrentFont()
    abstract fun loadFont(font: File)

    /*
    public void setStedWindow(STEDWindow stedWindow)
    {
        this.stedWindow = stedWindow;
    }
*/
    class FontsListModel : DefaultComboBoxModel<Any?>(), ChangeListener {
        private var fonts: Map<String, FontInfo>? = null
        fun setFonts(fonts: Map<String, FontInfo>?) {
            logger.entering(javaClass.name, "setFonts")
            this.fonts = fonts
            refreshFonts()
        }

        private fun refreshFonts() {
            logger.entering(javaClass.name, "refreshFonts")
            removeAllElements()
            val contents: Array<Any> = fonts!!.keys.toTypedArray()
            Arrays.sort(contents)
            for (newVar in contents) {
                addElement(newVar)
            }
        }

        /**
         * Invoked when the target of the listener has changed its state.
         *
         * @param e a ChangeEvent object
         */
        override fun stateChanged(e: ChangeEvent) {
            setFonts(Resources.fonts)
            fireContentsChanged(this, 0, fonts!!.size)
        }

        companion object {
            private val logger = Logger.getLogger(
                "sted.ui.FontKeypad\$FontsListModel"
            )
        }
    }

    //    abstract protected void addKeypadListener();
    class FontList
    /**
     * Creates a `JComboBox` that takes it's items from an
     * existing `ComboBoxModel`.  Since the
     * `ComboBoxModel` is provided, a combo box created using
     * this constructor does not create a default combo box model and may
     * impact how the insert, remove and add methods behave.
     *
     * @see DefaultComboBoxModel
     */
        : JComboBox<Any?>(), ChangeListener {
        /**
         * Invoked when the target of the listener has changed its state.
         *
         * @param e a ChangeEvent object
         */
        override fun stateChanged(e: ChangeEvent) {
            (model as FontsListModel).stateChanged(e)
            val font = (e as FontListChangeEvent).fontChanged
            if (font != null) selectedItem = font.name
            updateUI()
        }
    }
}