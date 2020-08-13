package sted.actions

import sted.STEDGUI
import java.awt.event.ActionEvent

open class ReOpenFontMapAction : OpenFontMapAction() {
    override fun actionPerformed(e: ActionEvent) {
        STEDGUI.busy()
        val tabDesktop = stedWindow.desktop
        //        if (JOptionPane.CANCEL_OPTION != tabDesktop.saveDirty())
//        {
        val fileName = getValue(NAME) as String
        tabDesktop.reopenFontMap(fileName)
        //        }
        STEDGUI.relax()
    }
}