package intellibitz.sted.launch;

import intellibitz.sted.util.FileHelper;
import intellibitz.sted.util.Resources;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;


public class STEDLogManager {

    private static LogManager logManager;

    private STEDLogManager() {
    }

    public static LogManager getLogmanager() {
        if (logManager == null) {
            logManager = LogManager.getLogManager();
            try {
                logManager.readConfiguration(
                        new BufferedInputStream(FileHelper.getInputStream
                                (new File(FileHelper.suffixFileSeparator(
                                        System.getProperty(Resources.LOG_PATH,
                                                "./log/")) +
                                        Resources
                                                .getResource(
                                                        Resources.LOG_CONFIG_NAME)))));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (SecurityException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        return logManager;
    }
}
