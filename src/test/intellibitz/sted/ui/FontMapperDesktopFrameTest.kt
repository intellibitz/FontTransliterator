package sted.ui

import kotlin.test.Test

class FontMapperDesktopFrameTest {
    private val stedWindow: STEDWindow? = null
//    @Before
    fun testSTEDWindow() {
//        stedWindow = new STEDWindow();
//        stedWindow.load();
    }

    @Test
    fun testFontMapperDesktopFrame() {
        val desktopFrame = DesktopFrame()
        desktopFrame.init()
    }
}