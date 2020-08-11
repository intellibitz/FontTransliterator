package sted.ui;

import org.jetbrains.annotations.NotNull;
import sted.actions.LoadFontAction;
import sted.event.*;
import sted.fontmap.FontInfo;
import sted.fontmap.FontMap;
import sted.io.Resources;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

/**
 * FontKeypad holds font dropdown and keypad for selecting characters
 */
public abstract class FontKeypad
        extends JPanel
        implements ItemListener,
        FontMapChangeListener,
        IKeypadEventSource {
    private final ArrayList<JButton> keys = new ArrayList<>();
    private FontMap fontMap;
    private FontList fontSelector;
    private JPanel keypad;
    private Font currentFont;
    private int KEY_COLUMNS = 6;
    private int FONT_MAX_INDEX = 65536;
    private KeypadEvent keypadEvent;
    private EventListenerList keypadListeners;

    protected FontKeypad() {
        super();
    }

    public void init() {
        final TitledBorder titledBorder =
                new TitledBorder(Resources.getResource(Resources.TITLE_KEYPAD));
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        setBorder(titledBorder);
        final GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;

        JButton loadFont = new JButton(new LoadFontAction(this));
        gridBagLayout.setConstraints(loadFont, gridBagConstraints);
        add(loadFont);

        fontSelector =
                new FontList(new FontsListModel(Resources.getFonts()));
        setCurrentFont((String) fontSelector.getItemAt(0));
        fontSelector.setSelectedItem(currentFont);
        fontSelector.addItemListener(this);
        gridBagLayout.setConstraints(fontSelector, gridBagConstraints);
        add(fontSelector);

        //

        gridBagConstraints.weightx = GridBagConstraints.RELATIVE;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        keypadListeners = new EventListenerList();
        keypadEvent = new KeypadEvent(this);

        final JComponent fontKeypad = getFontKeypad();
        gridBagLayout.setConstraints(fontKeypad, gridBagConstraints);
        //
        add(fontKeypad);
    }

    public void load() {
        String count = Resources.getSetting("keypad.column.count");
        if (count != null)
            KEY_COLUMNS = Integer.parseInt(count);
        String max = Resources.getSetting("font.char.maxindex");
        if (max != null)
            FONT_MAX_INDEX = Integer.parseInt(max);
    }

    public ArrayList<JButton> getKeys() {
        return keys;
    }

    public void itemStateChanged(ItemEvent e) {
        setCurrentFont(e.getItem().toString());
        resetKeypad();
    }

    public void stateChanged(FontMapChangeEvent e) {
        fontMap = e.getFontMap();
        setCurrentFont();
        resetKeypad();
    }

    public String getSelectedFont() {
        Object selectedItem = fontSelector.getSelectedItem();
        if (selectedItem == null) return null;
        return selectedItem.toString();
    }

    private JScrollPane getFontKeypad() {
        keypad = new JPanel();
        keypad.setBorder(BorderFactory.createEmptyBorder());
//        resetKeypad();
        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.getViewport().add(keypad);
        return jScrollPane;
    }

    private void resetKeypad() {
        keypad.removeAll();
        final GridBagLayout gridBagLayout = new GridBagLayout();
        keypad.setLayout(gridBagLayout);
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        final int numOfGlyphs = currentFont.getNumGlyphs();
        for (int i = 0, j = 0; i < FONT_MAX_INDEX && j < numOfGlyphs; i++) {
            final char c = (char) i;
            if (currentFont.canDisplay(c)) {
                final String cmd = Resources.EMPTY_STRING + c;
                final JButton keyButton;
                if (!keys.isEmpty() && j < keys.size()) {
                    keyButton = keys.get(j);
                } else {
                    keyButton = new JButton();
                    keys.add(j, keyButton);
                }
                // remove all the action listeners previously added
                // only 1 action listener to be added per button
                final ActionListener[] actionListeners =
                        keyButton.getActionListeners();
                if (actionListeners != null && actionListeners.length > 0) {
                    for (final ActionListener newVar : actionListeners) {
                        keyButton.removeActionListener(newVar);
                    }
                }
                keyButton.setFont(currentFont);
                keyButton.setText(cmd);
                gridBagConstraints.gridwidth = 1;
                if ((j + 1) % KEY_COLUMNS == 0) {
                    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
                }
                gridBagLayout.setConstraints(keyButton, gridBagConstraints);
                keypad.add(keyButton);
                j++;
            }
        }
//        addKeypadListener();
        fireKeypadReset();
        keypad.updateUI();
        // garbage collect
        System.gc();
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    public void fireKeypadReset() {
        // Guaranteed to return a non-null array
        final Object[] listeners = keypadListeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IKeypadListener.class) {
                // Lazily create the event:
                if (keypadEvent == null) {
                    keypadEvent = new KeypadEvent(this);
                }
                ((IKeypadListener) listeners[i + 1])
                        .keypadReset(keypadEvent);
            }
        }
    }

    public void addKeypadListener(@NotNull IKeypadListener keypadListener) {
        keypadListeners.add(IKeypadListener.class, keypadListener);
    }

    public void removeKeypadListener(IKeypadListener keypadListener) {
        keypadListeners.remove(IKeypadListener.class, keypadListener);
    }

    FontMap getFontMap() {
        return fontMap;
    }

    Font getCurrentFont() {
        return currentFont;
    }

    void setCurrentFont(String fontName) {
        FontInfo font = Resources.getFont(fontName);
        if (font != null)
            setCurrentFont(font.getFont());
    }

    void setCurrentFont(Font font) {
        if (font == null) {
            fontSelector.setSelectedIndex(0);
            Object item = fontSelector.getSelectedItem();
            if (item != null) {
                FontInfo info = Resources
                        .getFont(item.toString());
                if (info != null)
                    currentFont = info.getFont();
            }
        } else {
            currentFont = font;
            fontSelector.setSelectedItem(currentFont.getName());
        }
        setFont(currentFont);
    }

    public FontList getFontSelector() {
        return fontSelector;
    }

    abstract protected void setCurrentFont();

    abstract public void loadFont(File font);

/*
    public void setStedWindow(STEDWindow stedWindow)
    {
        this.stedWindow = stedWindow;
    }
*/

    static public class FontsListModel
            extends DefaultComboBoxModel
            implements ChangeListener {
        private static final Logger logger = Logger.getLogger(
                "sted.ui.FontKeypad$FontsListModel");
        private Map<String, FontInfo> fonts;

        public FontsListModel() {
            super();
        }

        public FontsListModel(Map<String, FontInfo> fonts) {
            this();
            setFonts(fonts);
        }

        public void setFonts(Map<String, FontInfo> fonts) {
            logger.entering(getClass().getName(), "setFonts");
            this.fonts = fonts;
            refreshFonts();
        }

        private void refreshFonts() {
            logger.entering(getClass().getName(), "refreshFonts");
            removeAllElements();
            final Object[] contents = fonts.keySet().toArray();
            Arrays.sort(contents);
            for (final Object newVar : contents) {
                addElement(newVar);
            }
        }

        /**
         * Invoked when the target of the listener has changed its state.
         *
         * @param e a ChangeEvent object
         */
        public void stateChanged(ChangeEvent e) {
            setFonts(Resources.getFonts());
            fireContentsChanged(this, 0, fonts.size());
        }
    }

//    abstract protected void addKeypadListener();

    static public class FontList
            extends JComboBox
            implements ChangeListener {
        /**
         * Creates a <code>JComboBox</code> that takes it's items from an
         * existing <code>ComboBoxModel</code>.  Since the
         * <code>ComboBoxModel</code> is provided, a combo box created using
         * this constructor does not create a default combo box model and may
         * impact how the insert, remove and add methods behave.
         *
         * @param aModel the <code>ComboBoxModel</code> that provides the
         *               displayed list of items
         * @see DefaultComboBoxModel
         */
        public FontList(ComboBoxModel aModel) {
            super(aModel);    //To change body of overriden methods use Options | File Templates.
        }

        /**
         * Invoked when the target of the listener has changed its state.
         *
         * @param e a ChangeEvent object
         */
        public void stateChanged(ChangeEvent e) {
            ((FontsListModel) getModel()).stateChanged(e);
            Font font = ((FontListChangeEvent) e).getFontChanged();
            if (font != null)
                setSelectedItem(font.getName());
            updateUI();
        }
    }

}

