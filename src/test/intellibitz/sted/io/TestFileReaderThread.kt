package sted.io

import java.io.File
import kotlin.test.Test

class TestFileReaderThread {
    @Test
    fun testViewToolBar() {
        val fileReaderThread = FileReaderThread()
        fileReaderThread.init(File("settings/sted.xml"))
        try {
            fileReaderThread.start()
        } catch (e: IllegalStateException) {
            println("Caught Exception")
            e.printStackTrace()
        }
        println("After Start")
    }
}