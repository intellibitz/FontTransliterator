package intellibitz.sted.actions;

import intellibitz.sted.event.FontMapChangeEvent;
import intellibitz.sted.event.FontMapChangeListener;
import intellibitz.sted.fontmap.FontMap;
import intellibitz.sted.ui.STEDWindow;

import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;

public class SaveFontMapAction
        extends TableModelListenerAction
        implements FontMapChangeListener {
    public SaveFontMapAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        final STEDWindow stedWindow = getSTEDWindow();
        stedWindow.getDesktop().saveAction();
        final FontMap fontMap = stedWindow.getDesktop()
                .getFontMap();
        fontMap.fireUndoEvent();
        fontMap.fireRedoEvent();
        fireStatusPosted("Saved");
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged(FontMapChangeEvent e) {
        setEnabled(isSaveable(e.getFontMap()));
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        final FontMap fontMap =
                getSTEDWindow().getDesktop()
                        .getFontMap();
//        setEnabled(!fontMap.isNew() && fontMap.isDirty());
        setEnabled(isSaveable(fontMap));
    }

    private boolean isSaveable(FontMap fontMap) {
        return (fontMap.isDirty() && fontMap.isFileWritable()) ||
                (fontMap.isNew() && fontMap.isDirty());
    }

}
