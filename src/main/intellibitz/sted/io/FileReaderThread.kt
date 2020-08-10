package sted.io

import sted.event.IStatusEventSource
import sted.event.IStatusListener
import sted.event.StatusEvent
import sted.event.ThreadEventSourceBase
import java.io.*
import java.util.logging.Logger

class FileReaderThread : ThreadEventSourceBase(), IStatusEventSource {
    lateinit var file: File
    var statusListener: IStatusListener? = null
    var statusEvent = StatusEvent(this)
    fun init(file: File) {
        this.file = file
    }

    override fun run() {
        fireThreadRunStarted()
        logger.entering(javaClass.name, "run")
        try {
            if (!file.isFile) {
                this.message = ("File is null")
                fireThreadRunFailed()
            } else {
                result = getFileContents(file)
                fireThreadRunFinished()
            }
        } catch (e: FileNotFoundException) {
            message=("Cannot Read File - File not found: " + e.message)
            logger.throwing(javaClass.name, "run", e)
            fireThreadRunFailed()
        } catch (e: IOException) {
            message=("Cannot Read File - IOException: " + e.message)
            logger.throwing(javaClass.name, "run", e)
            fireThreadRunFailed()
        }
        logger.exiting(javaClass.name, "run")
    }

    @Throws(IOException::class)
    private fun getFileContents(file: File): String {
        var bufferedReader: BufferedReader? = null
        var fileReader: FileReader? = null
        return try {
            fileReader = FileReader(file)
            val sz = file.length().toInt()
            val cbuf = CharArray(sz)
            if (sz > 0) {
                bufferedReader = BufferedReader(fileReader, sz)
                progressMaximum = sz
                var count = 0
                var len = 100
                if (len > sz - count) {
                    len = sz - count
                }
                var offset = count
                logger.finest("File Size: $sz")
                logger.finest("File Offset: $offset")
                logger.finest("File Length to be read: $len")
                while (bufferedReader.read(cbuf, offset, len) > 0) {
                    count += len
                    if (len > sz - count) {
                        len = sz - count
                    }
                    offset = count
                    progress = count
                    fireThreadRunning()
                }
            }
            String(cbuf)
        } finally {
            if (bufferedReader != null) {
                fileReader!!.close()
                bufferedReader.close()
            }
        }
    }

    override fun fireStatusPosted() {
        statusListener!!.statusPosted(statusEvent)
    }

    override fun addStatusListener(statusListener: IStatusListener) {
        this.statusListener = statusListener
    }

    companion object {
        private val logger = Logger.getLogger("sted.io.FileReaderThread")
    }
}