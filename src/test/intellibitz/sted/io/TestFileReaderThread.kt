package sted.io

import org.junit.Test

class TestFileReaderThread {
    @Test
    fun testViewToolBar() {
        val fileReaderThread = FileReaderThread()
        try {
            fileReaderThread.start()
        } catch (e: IllegalStateException) {
            println("Caught Exception")
            e.printStackTrace()
        }
        println("After Start")
    }
}