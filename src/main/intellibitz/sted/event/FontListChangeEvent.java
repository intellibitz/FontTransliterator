package intellibitz.sted.event;

import intellibitz.sted.fontmap.FontMap;

import javax.swing.event.ChangeEvent;
import java.awt.*;

public class FontListChangeEvent
        extends ChangeEvent {
    private Font font;
    private int fontIndex;

    public FontListChangeEvent(FontMap fontMap) {
        super(fontMap);
    }

    public int getFontIndex() {
        return fontIndex;
    }

    public void setFontIndex(int fontIndex) {
        this.fontIndex = fontIndex;
    }

    public Font getFontChanged() {
        return font;
    }

    public void setFontChanged(Font font) {
        this.font = font;
    }
}
