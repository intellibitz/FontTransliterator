package sted.ui;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.fontmap.FontMap;
import sted.io.FontMapXMLWriter;

import javax.swing.event.EventListenerList;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.logging.Logger;


/**
 * Contains FontMap and all its related Entities.
 */
public class DesktopModel {
    private File inputFile;
    private File outputFile;
    private FontMap fontMap;

    // the fontmap related events.. the model when change will fire these
    private FontMapChangeEvent fontMapChangeEvent;
    private EventListenerList eventListenerList;
    private static final Logger logger =
            Logger.getLogger(DesktopModel.class.getName());

    public DesktopModel() {
        super();
        eventListenerList = new EventListenerList();
    }


    public void clear() {
        fontMapChangeEvent = null;
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    public void fireFontMapChangedEvent() {
        // Guaranteed to return a non-null array
        final Object[] listeners = eventListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == FontMapChangeListener.class) {
                // Lazily create the event:
                if (fontMapChangeEvent == null) {
                    fontMapChangeEvent = new FontMapChangeEvent(getFontMap());
                }
                ((FontMapChangeListener) listeners[i + 1])
                        .stateChanged(fontMapChangeEvent);
            }
        }
    }

    public FontMap getFontMap() {
        return fontMap;
    }

    public void setFontMap(FontMap fontMap) {
        this.fontMap = fontMap;
    }

    public boolean isReadyForTransliteration
            () {
        boolean flag = false;
        if (getInputFile() != null
                && getOutputFile() != null
                && getFontMap().getFontMapFile() != null) {
            flag = true;
        }
        return flag;
    }


    public File getInputFile
            () {
        return inputFile;
    }

    public File getOutputFile
            () {
        return outputFile;
    }

    public void setInputFile
            (File file) {
        inputFile = file;
    }


    public void setOutputFile(File file) {
        outputFile = file;
    }

    public void addFontMapChangeListener
            (FontMapChangeListener fontMapChangeListener) {
        eventListenerList
                .add(FontMapChangeListener.class, fontMapChangeListener);
    }

    public void removeFontMapChangeListener
            (FontMapChangeListener fontMapChangeListener) {
        eventListenerList
                .remove(FontMapChangeListener.class, fontMapChangeListener);
    }


    public FontMap saveFontMap()
            throws TransformerException {
        FontMapXMLWriter.write(fontMap);
        fontMap.getEntries().clearUndoRedo();
        fontMap.setDirty(false);
        fireFontMapChangedEvent();
        return fontMap;
    }

}