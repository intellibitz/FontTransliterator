package sted.actions

import sted.STEDGUI
import sted.io.Resources
import sted.ui.AboutSTED
import sted.ui.HelpWindow
import sted.ui.MenuHandler
import java.awt.Component
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.UnsupportedLookAndFeelException

class LAFAction : STEDWindowAction() {
    override fun actionPerformed(e: ActionEvent) {
        try {
            val collection: MutableCollection<Component> = ArrayList()
            collection.add(stedWindow)
            val help = HelpWindow.instance
            collection.add(help)
            val aboutDialog = AboutSTED.instance
            collection.add(aboutDialog)
            val component: Component? = MenuHandler.popupMenus[Resources.MENU_POPUP_MAPPING]
            if (null != component) {
                collection.add(component)
            }
            STEDGUI.updateUIWithLAF(
                e.actionCommand,
                collection.iterator()
            )
        } catch (e1: ClassNotFoundException) {
            logger.throwing(javaClass.name, "actionPerformed", e1)
            fireMessagePosted(
                "Unable to Set LookAndFeel - Class Not Found: " +
                        e1.message
            )
            e1.printStackTrace() //To change body of catch statement use Options | File Templates.
        } catch (e1: InstantiationException) {
            logger.throwing(javaClass.name, "actionPerformed", e1)
            fireMessagePosted(
                "Unable to Set LookAndFeel - Cannot Instantiate: " +
                        e1.message
            )
            e1.printStackTrace() //To change body of catch statement use Options | File Templates.
        } catch (e1: IllegalAccessException) {
            logger.throwing(javaClass.name, "actionPerformed", e1)
            fireMessagePosted(
                "Unable to Set LookAndFeel - IllegalAccess: " +
                        e1.message
            )
            e1.printStackTrace() //To change body of catch statement use Options | File Templates.
        } catch (e1: UnsupportedLookAndFeelException) {
            logger.throwing(javaClass.name, "actionPerformed", e1)
            fireMessagePosted(
                "Unable to Set LookAndFeel - Unsupported: " +
                        e1.message
            )
            e1.printStackTrace() //To change body of catch statement use Options | File Templates.
        }
    }

}