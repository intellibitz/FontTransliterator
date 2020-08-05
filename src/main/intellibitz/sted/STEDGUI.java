package intellibitz.sted;

import intellibitz.sted.ui.AboutText;
import intellibitz.sted.ui.STEDWindow;
import intellibitz.sted.util.Resources;
import intellibitz.sted.widgets.SplashWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * STED GUI The Root of STED GUI Application
 */
public class STEDGUI {
    private static Logger logger = Logger.getLogger(STEDGUI.class.getName());
    private static STEDWindow stedWindow;


    private STEDGUI() {
        SplashWindow splashWindow = new SplashWindow(AboutText.getInstance());
        centerComponent(splashWindow);
        splashWindow.setVisible(true);
        STEDLogManager.getLogmanager().addLogger(logger);
        splashWindow.setProgress(10);

        stedWindow = new STEDWindow();
        stedWindow.addStatusListener(splashWindow);
        stedWindow.init();
        stedWindow.load();
        splashWindow.setProgress(90);
        stedWindow.setVisible(true);
        final String fileName = System.getProperty(Resources.FONTMAP_FILE);
        if (null != fileName && !Resources.EMPTY_STRING.equals(fileName)) {
            stedWindow.getDesktop()
                    .openFontMap(new File(fileName));
        } else {
//            File file = new File(Resources.getSampleFontMap());
//            stedWindow.getDesktop().openFontMap(file);
            stedWindow.getDesktop().newFontMap();
        }
        splashWindow.setProgress(100);
        splashWindow.dispose();
    }


    public static void main(String[] args) {
        try {
            new STEDGUI();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            logger.severe("Exception : " + e.getMessage());
            logger.throwing("intellibitz.sted.STEDGUI", "main", e);
            System.exit(-1);
        }
    }


    public static STEDWindow getSTEDWindow(Component component) {
        // if STEDWindow is already found.. return from cache
        if (null != stedWindow) {
            return stedWindow;
        }
        Component parent;
        Component src = component;
        do {
            parent = src.getParent();
            if (STEDWindow.class.isInstance(parent)) {
                stedWindow =
                        (STEDWindow) parent;
                return stedWindow;
            }
            src = parent;
        }
        while (parent != null);
        return null;
    }

    public static STEDWindow getSTEDWindow() {
        return stedWindow;
    }

    public static void busy() {
        stedWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public static void relax() {
        stedWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * A very nice trick is to center windows on screen
     *
     * @param component The <code>Component</code> to center
     */

    public static void centerComponent(Component component) {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension size = component.getSize();
        component.setLocation(new Point((dimension.width - size.width) / 2,
                (dimension.height - size.height) / 2));
    }

    public static void updateUIWithLAF(String lookAndFeel,
                                       Iterator<Component> iterator)
            throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(lookAndFeel);
        while (iterator.hasNext()) {
            final Component component = iterator.next();
            SwingUtilities.updateComponentTreeUI(component);
        }
    }

}
