package sted.ui;

import sted.event.FontListChangeEvent;
import sted.fontmap.FontMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;

public class FontKeypad2
        extends FontKeypad
        implements ChangeListener {

    public FontKeypad2() {
        super();
    }

    public void loadFont(File font) {
        final FontMap fontMap = getFontMap();
        if (fontMap != null) {
            fontMap.setFont2(font);
        }
    }

    protected void setCurrentFont() {
        super.setCurrentFont(getFontMap().getFont2());
    }

    protected void setCurrentFont(String font) {
        super.setCurrentFont(font);
        final FontMap fontMap = getFontMap();
        if (fontMap != null) {
            fontMap.setFont2(getCurrentFont());
        }
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e) {
        if (((FontListChangeEvent) e).getFontIndex() == 2) {
            getFontSelector().stateChanged(e);
            updateUI();
        }
    }

}
