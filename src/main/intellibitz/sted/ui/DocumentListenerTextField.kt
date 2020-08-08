package sted.ui;

import sted.fontmap.FontMap;
import sted.fontmap.SampleTextConverter;
import sted.io.Resources;
import sted.widgets.FontChangeTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentListenerTextField
        extends FontChangeTextField
        implements DocumentListener {
    private SampleTextConverter converter;
    private FontMap fontMap;
    private MapperPanel mapperPanel;
//    private STEDWindow stedWindow;

    public DocumentListenerTextField() {
        super();
    }

/*
    public DocumentListenerTextField(MapperPanel mapperPanel,
            STEDWindow stedWindow)
    {
        this();
        this.mapperPanel = mapperPanel;
        this.stedWindow = stedWindow;
    }
*/

    public void load() {

    }

    public void insertUpdate(DocumentEvent e) {
        convertSampleText(e);
    }

    public void removeUpdate(DocumentEvent e) {
        convertSampleText(e);
    }

    public void changedUpdate(DocumentEvent e) {
        convertSampleText(e);
    }

    private void convertSampleText(DocumentEvent e) {
        if (e.getDocument().getLength() > 0) {
            if (converter == null) {
                converter = new SampleTextConverter(mapperPanel);
            }
            converter.setFontMap(fontMap);
            final MenuHandler menuHandler = MenuHandler.getInstance();
            final JCheckBoxMenuItem preserve =
                    (JCheckBoxMenuItem) menuHandler.getMenuItem
                            (Resources.ACTION_PRESERVE_TAGS);
            converter.setHTMLAware(preserve.isSelected());
            final JCheckBoxMenuItem reverse =
                    (JCheckBoxMenuItem) menuHandler.getMenuItem
                            (Resources.ACTION_TRANSLITERATE_REVERSE);
            converter.setReverseTransliterate(reverse.isSelected());
            SwingUtilities.invokeLater(converter);
        }
    }

    public void setFontMap(FontMap fontMap) {
        this.fontMap = fontMap;
    }

    public void setFontMapperPanel(MapperPanel mapperPanel) {
        this.mapperPanel = mapperPanel;
    }

}

