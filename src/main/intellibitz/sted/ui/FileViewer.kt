package sted.ui

import sted.event.IThreadListener
import sted.event.ThreadEvent
import sted.io.FileReaderThread
import java.awt.Font
import java.io.File
import javax.swing.*

class FileViewer : JInternalFrame(), IThreadListener {
    private val editorPane: JEditorPane? = JEditorPane()
    private val fileReaderThread = FileReaderThread()
    private var file = File("")

    /*
    public FileViewer(Icon icon) {
        this();
        setFrameIcon(icon);
    }
*/
    fun init() {
        setTitle("")
        isResizable = false
        isClosable = false
        isMaximizable = false
        isIconifiable = false
        border = BorderFactory.createEtchedBorder()
        editorPane!!.isEditable = false
        val scroller = JScrollPane()
        scroller.viewport.add(editorPane)
        val contentPane = contentPane
        contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)
        contentPane.add(scroller)
        fileReaderThread.addThreadListener(this)
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
    }

    /**
     * Sets the font for this component.
     *
     * @param font the desired `Font` for this component
     * @see java.awt.Component.getFont
     *
     *
     * preferred: true bound: true attribute: visualUpdate true
     * description: The font for the component.
     */
    override fun setFont(font: Font) {
        super.setFont(
            font
        ) //To change body of overriden methods use Options | File Templates.
        if (editorPane != null) {
            editorPane.font = font
        }
    }

    fun setFileName(fileName: String) {
        setTitle(fileName)
        file = File(fileName)
    }

    fun addThreadListener(threadListener: IThreadListener?) {
        fileReaderThread.addThreadListener(threadListener!!)
    }

    fun readFile() {
        fileReaderThread.file = file
        SwingUtilities.invokeLater(fileReaderThread)
    }

    override fun threadRunStarted(threadEvent: ThreadEvent) {}
    override fun threadRunning(threadEvent: ThreadEvent) {}
    override fun threadRunFailed(threadEvent: ThreadEvent) {}
    override fun threadRunFinished(threadEvent: ThreadEvent) {
        editorPane!!.text = threadEvent.eventSource.result.toString()
    }
}