package intellibitz.sted.event;

import intellibitz.sted.fontmap.FontMap;

import javax.swing.event.ChangeEvent;

public class FontMapChangeEvent
        extends ChangeEvent {
    public FontMapChangeEvent(FontMap fontMap) {
        super(fontMap);
    }

    public FontMap getFontMap() {
        return (FontMap) getSource();
    }

}
