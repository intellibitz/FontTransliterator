package sted.actions

import java.awt.event.ActionEvent

open class OpenFontMapAction : STEDWindowAction() {
    override fun actionPerformed(e: ActionEvent) {
        //        STEDGUI.busy();
//        if (JOptionPane.CANCEL_OPTION !=
//                stedWindow.getDesktop().saveDirty())
//        {
        stedWindow.desktop.openFontMap()
        //        }
//        STEDGUI.relax();
    }
}