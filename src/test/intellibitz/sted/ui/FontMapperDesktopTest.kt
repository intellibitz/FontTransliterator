package sted.ui

import javax.swing.JFrame

class FontMapperDesktopTest {
    private val stedWindow: STEDWindow? = null

    //    @Before public void testSTEDWindow ()
    //    {
    //        stedWindow = new STEDWindow();
    //        stedWindow.load();
    //    }
    fun testFontMapperDesktop() {
        val testFrame = JFrame("Testing")
        testFrame.setSize(300, 300)
        val tabDesktop = TabDesktop()
        tabDesktop.init()
        tabDesktop.createFontMapperDesktopFrame()
        tabDesktop.isVisible = true
        testFrame.contentPane.add(tabDesktop)
        testFrame.isVisible = true
        //        tabDesktop.init();
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val fontMapperDesktopTest = FontMapperDesktopTest()
            fontMapperDesktopTest.testFontMapperDesktop()
        }
    }
}