package sted.actions

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.fontmap.FontMap
import java.awt.event.ActionEvent
import javax.swing.event.TableModelEvent

class SaveFontMapAction : TableModelListenerAction(), FontMapChangeListener {
    override fun actionPerformed(e: ActionEvent) {
        stedWindow.desktop.saveAction()
        val fontMap = stedWindow.desktop
            .fontMap
        fontMap.fireUndoEvent()
        fontMap.fireRedoEvent()
        fireStatusPosted("Saved")
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param fontMapChangeEvent a ChangeEvent object
     */
    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        isEnabled = isSaveable(fontMapChangeEvent.fontMap)
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        val fontMap: FontMap = stedWindow.desktop
            .fontMap
        //        setEnabled(!fontMap.isNew() && fontMap.isDirty());
        isEnabled = isSaveable(fontMap)
    }

    private fun isSaveable(fontMap: FontMap): Boolean {
        return fontMap.isDirty && fontMap.isFileWritable ||
                fontMap.isNew && fontMap.isDirty
    }
}