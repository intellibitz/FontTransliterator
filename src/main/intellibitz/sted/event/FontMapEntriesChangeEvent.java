package intellibitz.sted.event;

import intellibitz.sted.fontmap.FontMapEntries;

import javax.swing.event.ChangeEvent;

public class FontMapEntriesChangeEvent
        extends ChangeEvent {
    public FontMapEntriesChangeEvent(FontMapEntries fontMapEntries) {
        super(fontMapEntries);
    }

    public FontMapEntries getFontMapEntries() {
        return (FontMapEntries) getSource();
    }

}