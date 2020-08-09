package sted.ui

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.event.IStatusListener
import sted.event.StatusEvent
import sted.fontmap.FontMap
import sted.io.Resources
import sted.io.Resources.cleanIcon
import sted.io.Resources.dirtyIcon
import sted.io.Resources.getSetting
import sted.io.Resources.getSystemResourceIcon
import sted.io.Resources.lockIcon
import sted.io.Resources.unLockIcon
import sted.widgets.GCButton
import sted.widgets.MemoryBar
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.util.*
import javax.swing.*
import javax.swing.Timer
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

class StatusPanel : JPanel(), TableModelListener, ListSelectionListener, FontMapChangeListener, IStatusListener {
    private val memoryBar = MemoryBar()
    val progressBar = JProgressBar(JProgressBar.HORIZONTAL)
    private val status = JLabel()
    private val counter = JLabel()
    private val flag = JLabel()
    private val lock = JLabel()
    lateinit var stedWindow: STEDWindow

    fun load(stedWindow: STEDWindow) {
        this.stedWindow = stedWindow
        border = BorderFactory.createLineBorder(foreground)
        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.weighty = 1.0
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.gridheight = 1
        gridBagConstraints.gridwidth = 1
        gridBagConstraints.ipadx = 1
        gridBagLayout.setConstraints(status, gridBagConstraints)
        add(status)
        gridBagConstraints.weightx = 0.0
        gridBagLayout.setConstraints(progressBar, gridBagConstraints)
        add(progressBar)
        val panel1 = JPanel()
        panel1.border = BorderFactory.createLineBorder(foreground)
        initCounter()
        panel1.add(counter)
        gridBagLayout.setConstraints(panel1, gridBagConstraints)
        add(panel1)
        val panel2 = JPanel()
        panel2.border = BorderFactory.createLineBorder(foreground)
        panel2.add(flag)
        gridBagLayout.setConstraints(panel2, gridBagConstraints)
        add(panel2)
        val panel3 = JPanel()
        panel3.border = BorderFactory.createLineBorder(foreground)
        panel3.add(lock)
        gridBagLayout.setConstraints(panel3, gridBagConstraints)
        add(panel3)
        gridBagLayout.setConstraints(memoryBar, gridBagConstraints)
        add(memoryBar)
        val imageIcon: Icon? = getSystemResourceIcon(getSetting("icon.gc"))
        val gcButton = GCButton()
        gcButton.load(
            imageIcon, getSystemResourceIcon(
                getSetting(Resources.ICON_GC2)
            )
        )
        gridBagLayout.setConstraints(gcButton, gridBagConstraints)
        add(gcButton)
    }

    fun runMemoryBar() {
        // update memory status every 2 seconds
        val timer = Timer(2000, memoryBar)
        timer.start()
    }

    fun clear() {
        clearStatus()
        clearProgress()
        initCounter()
        setCleanFlag()
        setLockFlag(true)
    }

    fun setStatus(msg: String?) {
        status.text = Resources.SPACE + msg
    }

    private fun clearStatus() {
        status.text = Resources.EMPTY_STRING
    }

    fun clearProgress() {
        progressBar.isStringPainted = false
        progressBar.minimum = 0
        progressBar.maximum = 0
        progressBar.value = 0
    }

    private fun initCounter() {
        counter.text = COUNTER_INIT
    }

    private fun setTotalCount(total: Int) {
        if (total < 1) {
            initCounter()
        } else {
            val stringTokenizer = StringTokenizer(counter.text, Resources.COLON)
            val stringBuffer = StringBuilder()
            if (stringTokenizer.hasMoreTokens()) {
                stringBuffer.append(stringTokenizer.nextToken())
            } else {
                stringBuffer.append(0)
            }
            stringBuffer.append(Resources.COLON)
            stringBuffer.append(total)
            counter.text = stringBuffer.toString()
        }
    }

    private fun setCurrentCount(curr: Int) {
        if (curr >= 0) {
            val stringBuffer = StringBuilder()
            stringBuffer.append(curr + 1)
            stringBuffer.append(Resources.COLON)
            val stringTokenizer = StringTokenizer(counter.text, Resources.COLON)
            if (stringTokenizer.hasMoreTokens()) {
                // skip the current count
                stringTokenizer.nextToken()
                // keep the total count
                stringBuffer.append(stringTokenizer.nextToken())
            }
            counter.text = stringBuffer.toString()
        }
    }

    fun setNeatness(fontMap: FontMap) {
        clearProgress()
        setLockFlag(!fontMap.isFileWritable)
        if (fontMap.isDirty) {
            setDirtyFlag()
        } else {
            setCleanFlag()
        }
    }

    private fun setCleanFlag() {
        flag.icon = cleanIcon
    }

    private fun setDirtyFlag() {
        flag.icon = dirtyIcon
    }

    fun setLockFlag(flag: Boolean) {
        if (flag) {
            lock.icon = lockIcon
        } else {
            lock.icon = unLockIcon
        }
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        val tableModel = e.source as TableModel
        setTotalCount(tableModel.rowCount)
        setNeatness(stedWindow.desktop.fontMap)
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    override fun valueChanged(e: ListSelectionEvent) {
        val listSelectionModel = e.source as ListSelectionModel
        setCurrentCount(listSelectionModel.maxSelectionIndex)
    }

    override fun stateChanged(e: FontMapChangeEvent) {
        setNeatness(e.fontMap)
    }

    override fun statusPosted(event: StatusEvent) {
        setStatus(event.status)
    }

    companion object {
        private const val COUNTER_INIT = "0:0"
    }
}