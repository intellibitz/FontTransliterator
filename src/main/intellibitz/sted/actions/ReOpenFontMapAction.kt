package sted.actions

import sted.STEDGUI.Companion.busy
import sted.STEDGUI.Companion.relax
import java.awt.event.ActionEvent

open class ReOpenFontMapAction : OpenFontMapAction() {
    override fun actionPerformed(e: ActionEvent) {
        busy()
        val tabDesktop = stedWindow.desktop
        //        if (JOptionPane.CANCEL_OPTION != tabDesktop.saveDirty())
//        {
        val fileName = getValue(NAME) as String
        tabDesktop.reopenFontMap(fileName)
        //        }
        relax()
    }
}