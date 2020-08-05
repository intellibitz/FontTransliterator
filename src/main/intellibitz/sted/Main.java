package intellibitz.sted;

import java.util.logging.Logger;

/**
 * Main<br> Invoke STED Console or the GUI based on the arguments
 */
public class Main {

    private Main() {
    }

    /**
     * Entry point
     *
     * @param args the command line arguments for the application
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("intellibitz.sted.Main");
        STEDLogManager.getLogmanager().addLogger(logger);
        if (args != null && args.length > 0) {
            final String param1 = args[0];
            // launch Console
            if (param1.toLowerCase().startsWith("-c")) {
                logger.info("Launching STED Console: ");
                final int len = args.length;
                final String[] args1 = new String[len - 1];
                System.arraycopy(args, 1, args1, 0, len - 1);
                STEDConsole.main(args1);
            }
        } else {
            logger.info("Launching STED GUI: ");
            // launch GUI
            STEDGUI.main(args);
        }
    }

}
