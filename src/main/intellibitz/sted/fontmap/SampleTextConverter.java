package sted.fontmap;

import sted.ui.MapperPanel;
import sted.io.Resources;


public class SampleTextConverter
        extends Converter {
    private MapperPanel mapperPanel;

    public SampleTextConverter(MapperPanel mapperPanel) {
        super();
        this.mapperPanel = mapperPanel;
    }

    public void run() {
        final String input = mapperPanel.getInputText().getText();
        mapperPanel.getOutputText().setText(Resources.EMPTY_STRING);
        if (isReady() && input != null && input.length() > 0) {
            mapperPanel.getOutputText().setText
                    (getTransliterate().parseLine(input));
        }
    }

}

