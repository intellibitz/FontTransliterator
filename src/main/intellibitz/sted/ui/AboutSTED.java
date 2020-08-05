package intellibitz.sted.ui;

import intellibitz.sted.util.Resources;
import intellibitz.sted.widgets.AboutDialog;

/**
 * Displays the About message.
 */
public class AboutSTED
        extends AboutDialog {

    private static AboutSTED aboutSTED;

    public static synchronized AboutSTED getInstance() {
        if (aboutSTED == null) {
            aboutSTED = new AboutSTED();
        }
        return aboutSTED;
    }

    private AboutSTED() {
        super(Resources.getResource(Resources.TITLE_ABOUT_STED),
                AboutText.getInstance());
    }

}