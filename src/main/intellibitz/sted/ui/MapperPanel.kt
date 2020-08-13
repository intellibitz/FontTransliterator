package sted.ui

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.io.Resources.getResource
import sted.widgets.FontChangeTextField
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class MapperPanel : JPanel(), FontMapChangeListener {
    val inputText = FontChangeTextField()
    val outputText = DocumentListenerTextField()
    val fontKeypad1 = FontKeypad1()
    val mappingEntryPanel = MappingEntryPanel()
    val fontKeypad2 = FontKeypad2()

    /*
       public void convertSampleText() {
           setSampleInput(inputText.getText());
       }
   */  val previewPanel = JPanel()
    fun init() {
        border = BorderFactory.createEtchedBorder()
        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        val gridBagConstraints = GridBagConstraints()
        fontKeypad1.init()
        gridBagConstraints.weightx = 0.0
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.gridwidth = 1
        gridBagConstraints.fill = GridBagConstraints.VERTICAL
        gridBagLayout.setConstraints(fontKeypad1, gridBagConstraints)
        add(fontKeypad1)
        mappingEntryPanel.layout = gridBagLayout
        mappingEntryPanel.init()
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.gridwidth = 1
        gridBagLayout.setConstraints(mappingEntryPanel, gridBagConstraints)
        add(mappingEntryPanel)
        fontKeypad2.init()
        gridBagConstraints.weightx = 0.0
        gridBagConstraints.fill = GridBagConstraints.VERTICAL
        gridBagConstraints.gridwidth = 1
        gridBagLayout.setConstraints(fontKeypad2, gridBagConstraints)
        add(fontKeypad2)
        val titledBorder = BorderFactory.createTitledBorder(
            getResource("title.mapping.preview")
        )
        titledBorder.titleJustification = TitledBorder.CENTER
        previewPanel.border = titledBorder
        val gridBagLayout2 = GridBagLayout()
        previewPanel.layout = gridBagLayout2
        val gbc = GridBagConstraints()
        inputText.setSize(width, (height * 1.5).toInt())
        val inputLabel = JLabel("Input:  ")
        gbc.fill = GridBagConstraints.VERTICAL
        gbc.weightx = 0.0
        gridBagLayout2.setConstraints(inputLabel, gbc)
        previewPanel.add(inputLabel)
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        gridBagLayout2.setConstraints(inputText, gbc)
        previewPanel.add(inputText)
        outputText.mapperPanel = this
        //        outputText.setBackground(Color.BLACK);
        outputText.foreground = Color.GRAY
        outputText.setSize(width, (height * 1.5).toInt())
        outputText.isEditable = false
        val outputLabel = JLabel("Output: ")
        gbc.fill = GridBagConstraints.VERTICAL
        gbc.gridy = 1
        gbc.weightx = 0.0
        gridBagLayout2.setConstraints(outputLabel, gbc)
        previewPanel.add(outputLabel)
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        gridBagLayout2.setConstraints(outputText, gbc)
        previewPanel.add(outputText)
        inputText.document.addDocumentListener(outputText)
        gridBagConstraints.gridy = 1
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.weighty = 0.0
        gridBagConstraints.gridwidth = 3
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagLayout.setConstraints(previewPanel, gridBagConstraints)
        add(previewPanel)
    }

    fun load() {
        fontKeypad1.fontSelector.addItemListener(mappingEntryPanel.word1)
        fontKeypad1.fontSelector.addItemListener(inputText)
        fontKeypad1.addKeypadListener(mappingEntryPanel.word1)
        fontKeypad2.fontSelector.addItemListener(mappingEntryPanel.word2)
        fontKeypad2.fontSelector.addItemListener(outputText)
        fontKeypad2.addKeypadListener(mappingEntryPanel.word2)
        fontKeypad1.load()
        fontKeypad2.load()
        mappingEntryPanel.load()
        outputText.load()
    }

    fun clear() {
        mappingEntryPanel.clearPreviewDisplay()
        mappingEntryPanel.clear()
        outputText.text = ""
    }

    fun setSampleInput(text: String?) {
        inputText.text = text
    }

    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        val f = fontMapChangeEvent.fontMap
        inputText.font = f.font1
        outputText.font = f.font2
        outputText.fontMap = f
        inputText.text = inputText.text
    }
}