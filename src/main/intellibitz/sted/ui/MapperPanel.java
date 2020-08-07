package sted.ui;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.fontmap.FontMap;
import sted.io.Resources;
import sted.widgets.FontChangeTextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MapperPanel
        extends JPanel
        implements FontMapChangeListener {
    private FontKeypad1 fontKeypad1;
    private MappingEntryPanel mappingEntryPanel;
    private FontKeypad2 fontKeypad2;
    private JPanel previewPanel;
    private FontChangeTextField inputText;
    private DocumentListenerTextField outputText;

    public MapperPanel() {
        super();
    }

    public void init() {
        setBorder(BorderFactory.createEtchedBorder());
        final GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();

        fontKeypad1 = new FontKeypad1();
        fontKeypad1.init();

        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagLayout.setConstraints(fontKeypad1, gridBagConstraints);

        add(fontKeypad1);

        mappingEntryPanel = new MappingEntryPanel();
        mappingEntryPanel.setLayout(gridBagLayout);

        mappingEntryPanel.init();
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = 1;
        gridBagLayout.setConstraints(mappingEntryPanel, gridBagConstraints);

        add(mappingEntryPanel);

        fontKeypad2 = new FontKeypad2();
        fontKeypad2.init();

        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.gridwidth = 1;
        gridBagLayout.setConstraints(fontKeypad2, gridBagConstraints);

        add(fontKeypad2);

        previewPanel = new JPanel();
        final TitledBorder titledBorder = BorderFactory.createTitledBorder(
                Resources.getResource(Resources.TITLE_MAPPING_PREVIEW));
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        previewPanel.setBorder(titledBorder);
        final GridBagLayout gridBagLayout2 = new GridBagLayout();
        previewPanel.setLayout(gridBagLayout2);
        final GridBagConstraints gbc = new GridBagConstraints();

        inputText = new FontChangeTextField();
        inputText.setSize(getWidth(), (int) (getHeight() * 1.5));
        JLabel inputLabel = new JLabel("Input:  ");

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gridBagLayout2.setConstraints(inputLabel, gbc);
        previewPanel.add(inputLabel);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gridBagLayout2.setConstraints(inputText, gbc);
        previewPanel.add(inputText);

        outputText = new DocumentListenerTextField();
//        outputText.setBackground(Color.BLACK);
        outputText.setForeground(Color.GRAY);
        outputText.setSize(getWidth(), (int) (getHeight() * 1.5));
        outputText.setEditable(false);

        JLabel outputLabel = new JLabel("Output: ");

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gridBagLayout2.setConstraints(outputLabel, gbc);
        previewPanel.add(outputLabel);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gridBagLayout2.setConstraints(outputText, gbc);
        previewPanel.add(outputText);

        inputText.getDocument().addDocumentListener(outputText);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagLayout.setConstraints(previewPanel, gridBagConstraints);

        add(previewPanel);

    }

    public void load() {
        loadPreviewPanel();
        fontKeypad1.getFontSelector()
                .addItemListener(mappingEntryPanel.getWord1());
        fontKeypad1.getFontSelector().addItemListener(inputText);
        fontKeypad1.addKeypadListener(mappingEntryPanel.getWord1());

        fontKeypad2.getFontSelector()
                .addItemListener(mappingEntryPanel.getWord2());
        fontKeypad2.getFontSelector().addItemListener(outputText);
        fontKeypad2.addKeypadListener(mappingEntryPanel.getWord2());

        fontKeypad1.load();
        fontKeypad2.load();
        mappingEntryPanel.load();
        outputText.load();
    }

    public void clear() {
        mappingEntryPanel.clearPreviewDisplay();
        mappingEntryPanel.clear();
        outputText.setText(Resources.EMPTY_STRING);
    }


    public void loadPreviewPanel() {
        outputText.setFontMapperPanel(this);
    }

    public void setSampleInput(String text) {
        inputText.setText(text);
    }

    public void convertSampleText() {
        setSampleInput(inputText.getText());
    }

    public JPanel getPreviewPanel() {
        return previewPanel;
    }

    public FontKeypad1 getFontKeypad1() {
        return fontKeypad1;
    }

    public MappingEntryPanel getMappingEntryPanel() {
        return mappingEntryPanel;
    }

    public FontKeypad2 getFontKeypad2() {
        return fontKeypad2;
    }

    public FontChangeTextField getInputText() {
        return inputText;
    }

    public DocumentListenerTextField getOutputText() {
        return outputText;
    }

    public void stateChanged(FontMapChangeEvent e) {
        FontMap f = e.getFontMap();
        inputText.setFont(f.getFont1());
        outputText.setFont(f.getFont2());
        outputText.setFontMap(f);
        inputText.setText(inputText.getText());
    }

}


