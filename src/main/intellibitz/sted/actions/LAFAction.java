package sted.actions;

import sted.STEDGUI;
import sted.ui.AboutSTED;
import sted.ui.HelpWindow;
import sted.ui.STEDWindow;
import sted.ui.MenuHandler;
import sted.io.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class LAFAction
        extends STEDWindowAction {
    private static final Logger logger =
            Logger.getLogger("sted.actions.LAFAction");

    public LAFAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            final Collection<Component> collection = new ArrayList<Component>();
            final STEDWindow stedWindow = getSTEDWindow();
            collection.add(stedWindow);
            final HelpWindow help = HelpWindow.getInstance();
            if (null != help) {
                collection.add(help);
            }
            final AboutSTED aboutDialog = AboutSTED.Companion.getInstance();
            if (null != aboutDialog) {
                collection.add(aboutDialog);
            }
            final Component component = MenuHandler.getInstance()
                    .getPopupMenu(Resources.MENU_POPUP_MAPPING);
            if (null != component) {
                collection.add(component);
            }
            STEDGUI.updateUIWithLAF(e.getActionCommand(),
                    collection.iterator());
        } catch (ClassNotFoundException e1) {
            logger.throwing(getClass().getName(), "actionPerformed", e1);
            fireMessagePosted("Unable to Set LookAndFeel - Class Not Found: " +
                    e1.getMessage());
            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (InstantiationException e1) {
            logger.throwing(getClass().getName(), "actionPerformed", e1);
            fireMessagePosted(
                    "Unable to Set LookAndFeel - Cannot Instantiate: " +
                            e1.getMessage());
            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (IllegalAccessException e1) {
            logger.throwing(getClass().getName(), "actionPerformed", e1);
            fireMessagePosted("Unable to Set LookAndFeel - IllegalAccess: " +
                    e1.getMessage());
            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (UnsupportedLookAndFeelException e1) {
            logger.throwing(getClass().getName(), "actionPerformed", e1);
            fireMessagePosted("Unable to Set LookAndFeel - Unsupported: " +
                    e1.getMessage());
            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

}
