package intellibitz.sted.event;

import intellibitz.sted.fontmap.Converter;

public class TransliterateEvent
        extends ThreadEvent {
    public TransliterateEvent(Converter converter) {
        super(converter);
    }

    public Converter getConverter() {
        return (Converter) getSource();
    }

}
