package sted.ui

import sted.actions.EntryAction
import sted.actions.EntryClearAction
import sted.actions.TableModelListenerAction
import sted.actions.TableRowsSelectAction
import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.event.MappingPopupListener
import sted.fontmap.FontMap
import sted.fontmap.FontMapEntry
import sted.io.Resources.getResource
import sted.widgets.DocumentListenerButton
import sted.widgets.FontChangeTextField
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.event.*

class MappingEntryPanel : JPanel(), FontMapChangeListener, ItemListener, ListSelectionListener, DocumentListener {
    private val followedCombo: JComboBox<String> = JComboBox<String>()
    private val precededCombo: JComboBox<String> = JComboBox<String>()
    private val sym1Combo: JComboBox<String> = JComboBox<String>()
    private val sym2Combo: JComboBox<String> = JComboBox<String>()
    val mappingTableModel = MappingTableModel()
    val mappingRules = MappingRulesPanel()
    val word1 = FontChangeTextField()
    val word2 = FontChangeTextField()
    val clearButton = DocumentListenerButton()
    private val addButton = DocumentListenerButton()
    val entryAction = EntryAction()
    private val entryTable = JTable()
    val splitPane = JSplitPane()
    private val directMapPopupListener = MappingPopupListener()
    lateinit var fontMap: FontMap

    fun init() {
        val titledBorder = BorderFactory.createTitledBorder(
            getResource("title.mapping")
        )
        titledBorder.titleJustification = TitledBorder.CENTER
        border = titledBorder
        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        val preview = createWordEntryPanel()
        gridBagLayout.setConstraints(preview, gridBagConstraints)
        //
        add(preview)
        splitPane.orientation = JSplitPane.VERTICAL_SPLIT
        splitPane.isOneTouchExpandable = false
        splitPane.setDividerLocation(0.7)
        splitPane.dividerSize = 0
        // the top component gets all the extra spaces
        splitPane.resizeWeight = 1.0
        initTable()
        val scroller = JScrollPane(entryTable)
        gridBagConstraints.weighty = 1.0
        gridBagLayout.setConstraints(splitPane, gridBagConstraints)
        //
        splitPane.topComponent = scroller
        mappingRules.init()
        mappingTableModel.addTableModelListener(mappingRules)
        entryTable.selectionModel.addListSelectionListener(this)
        entryTable.selectionModel.addListSelectionListener(mappingRules)
        //
        splitPane.bottomComponent = mappingRules
        add(splitPane)
        word1.requestFocus()
    }

    fun load() {
        mappingRules.load()
        directMapPopupListener.load()
        loadTable()
        entryTable.selectionModel.addListSelectionListener(this)
        entryTable.selectionModel.addListSelectionListener(mappingRules)
        word1.requestFocus()
    }

    private fun initTable() {
        // Create a model of the data.
        entryTable.model = mappingTableModel
        entryTable.setDefaultRenderer(Any::class.java, MappingTableRenderer())
        entryTable.cellSelectionEnabled = true
        entryTable.columnSelectionAllowed = false
        entryTable.showVerticalLines = false
        entryTable.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
        entryTable.tableHeader.reorderingAllowed = false
        sym1Combo.isEditable = true
        sym2Combo.isEditable = true
        followedCombo.isEditable = true
        precededCombo.isEditable = true
        entryTable.columnModel.getColumn(0).cellEditor = DefaultCellEditor(sym1Combo)
        entryTable.columnModel.getColumn(2).cellEditor = DefaultCellEditor(sym2Combo)
        entryTable.columnModel.getColumn(5).cellEditor = DefaultCellEditor(followedCombo)
        entryTable.columnModel.getColumn(6).cellEditor = DefaultCellEditor(precededCombo)
        entryTable.addMouseListener(directMapPopupListener)
        mappingTableModel.addTableModelListener(mappingRules)
        mappingTableModel.addTableModelListener(word1)
    }

    private fun loadTable() {
        addTableModelListeners()
        setTableColumnWidth()
    }

    private fun addTableModelListeners() {
        for (action in MenuHandler.actions.values) {
            if (TableModelListenerAction::class.java.isInstance(action)) {
                addTableModelListener(action as TableModelListener)
            }
            if (TableRowsSelectAction::class.java.isInstance(action)) {
                addListSelectionListener(action as ListSelectionListener)
                (action as TableRowsSelectAction).table = entryTable
            }
        }
    }

    fun addTableModelListener(tableModelListener: TableModelListener?) {
        mappingTableModel.addTableModelListener(tableModelListener)
    }

    private fun addListSelectionListener(
        listSelectionListener: ListSelectionListener?
    ) {
        entryTable.selectionModel
            .addListSelectionListener(listSelectionListener)
    }

    private fun setTableColumnWidth() {
        val count = mappingTableModel.columnCount
        for (i in 0 until count) when {
            else -> {
                entryTable.columnModel.getColumn(i).preferredWidth = mappingTableModel.getColumnName(i).length
                entryTable.columnModel.getColumn(i).sizeWidthToFit()
                entryTable.tableHeader.columnModel.getColumn(i).preferredWidth = mappingTableModel
                    .getColumnName(i).length
                entryTable.tableHeader.columnModel.getColumn(i)
                    .sizeWidthToFit()
            }
        }
    }

/*
    fun setFontMap(fontMap: FontMap?) {
        this.fontMap = fontMap
    }
*/

    fun updateUIData() {
        reset()
        firePreviewTableDataChanged()
        sym1Combo.updateUI()
        sym2Combo.updateUI()
        precededCombo.updateUI()
        followedCombo.updateUI()
        updateUI()
    }

    private fun reset() {
        clear()
        word1.font = fontMap.font1
        word2.font = fontMap.font2
        precededCombo.addItem("")
        followedCombo.addItem("")
        sym1Combo.addItem("")
        sym2Combo.addItem("")
        val iterator = fontMap.entries.allWords
        while (iterator.hasNext()) {
            val next = iterator.next()
            sym2Combo.addItem(next)
            precededCombo.addItem(next)
            followedCombo.addItem(next)
        }
        val iterator2 = fontMap.entries.getWord2()
        while (iterator2.hasNext()) {
            sym1Combo.addItem(iterator2.next())
        }
        sym1Combo.font = fontMap.font1
        sym2Combo.font = fontMap.font2
        precededCombo.font = fontMap.font1
        followedCombo.font = fontMap.font1
        //        mappingTableModel.setFontMap(fontMap);
        mappingTableModel.loadFontMapEntries(fontMap)
        val fontPreviewTableRenderer = MappingTableRenderer()
        fontPreviewTableRenderer.fontMap = fontMap
        entryTable.setDefaultRenderer(Any::class.java, fontPreviewTableRenderer)
        setTableColumnWidth()
    }

    fun clear() {
        sym1Combo.removeAllItems()
        sym2Combo.removeAllItems()
        followedCombo.removeAllItems()
        precededCombo.removeAllItems()
    }

    private fun createWordEntryPanel(): JPanel {
        val jPanel = JPanel()
        jPanel.layout = BoxLayout(jPanel, BoxLayout.X_AXIS)
        word1.horizontalAlignment = JTextField.RIGHT
        //
        jPanel.add(word1)
        val jLabel = JLabel(" = ")
        jLabel.horizontalAlignment = JLabel.CENTER
        //
        jPanel.add(jLabel)
        word2.horizontalAlignment = JTextField.LEFT
        //
        jPanel.add(word2)
        val sAdd = getResource("label.add")
        addButton.text = sAdd
        addButton.isEnabled = false
        entryAction.putValue(Action.NAME, sAdd)
        entryAction.putValue(Action.SHORT_DESCRIPTION, "Add Mapping")
        entryAction.putValue(Action.MNEMONIC_KEY, 'A'.toInt())
        entryAction.putValue(Action.ACTION_COMMAND_KEY, sAdd)
        entryAction.mappingEntryPanel = this
        addButton.addActionListener(entryAction)
        addButton.addKeyListener(entryAction)
        //
        jPanel.add(addButton)
        val sClear = getResource("label.clear")
        clearButton.text = sClear
        clearButton.isEnabled = false
        val clearFontMapEntryInPreviewAction = EntryClearAction()
        clearFontMapEntryInPreviewAction.putValue(Action.NAME, sClear)
        clearFontMapEntryInPreviewAction
            .putValue(Action.SHORT_DESCRIPTION, "Clear Mapping")
        clearFontMapEntryInPreviewAction
            .putValue(Action.MNEMONIC_KEY, 'C'.toInt())
        clearFontMapEntryInPreviewAction
            .putValue(Action.ACTION_COMMAND_KEY, sClear)
        clearFontMapEntryInPreviewAction.mappingEntryPanel = this
        clearButton.addActionListener(clearFontMapEntryInPreviewAction)
        //
        jPanel.add(clearButton)
        word1.document.addDocumentListener(clearButton)
        word2.document.addDocumentListener(clearButton)
        word1.document.addDocumentListener(this)
        word2.document.addDocumentListener(this)
        word2.addKeyListener(entryAction)
        return jPanel
    }

    val listSelectionModel: ListSelectionModel
        get() = entryTable.selectionModel

    fun clearPreviewDisplay() {
        word1.text = ""
        word2.text = ""
    }

    private fun firePreviewTableDataChanged() {
        clearPreviewDisplay()
        (entryTable.model as MappingTableModel).fireTableDataChanged()
    }

    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        fontMap = fontMapChangeEvent.fontMap
        updateUIData()
    }

    override fun itemStateChanged(e: ItemEvent) {
        reset()
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    override fun valueChanged(e: ListSelectionEvent) {
        val listSelectionModel = e.source as ListSelectionModel
        val row = listSelectionModel.minSelectionIndex
        if (row > -1) {
            showEntry(mappingTableModel.getValueAt(row))
        }
    }

    private fun showEntry(valueAt: FontMapEntry?) {
        word1.text = valueAt!!.from
        word2.text = valueAt.to
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    override fun changedUpdate(e: DocumentEvent) {
        toggleAdd()
    }

    /**
     * Gives notification that there was an insert into the document.  The range
     * given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    override fun insertUpdate(e: DocumentEvent) {
        toggleAdd()
    }

    /**
     * Gives notification that a portion of the document has been removed.  The
     * range is given in terms of what the view last saw (that is, before
     * updating sticky positions).
     *
     * @param e the document event
     */
    override fun removeUpdate(e: DocumentEvent) {
        toggleAdd()
    }

    private fun toggleAdd() {
        addButton.isEnabled = word1.text.isNotEmpty() &&
                word2.text.isNotEmpty()
    }
}