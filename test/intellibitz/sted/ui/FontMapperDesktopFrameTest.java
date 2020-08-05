package intellibitz.sted.ui;

import org.junit.Before;
import org.junit.Test;

public class FontMapperDesktopFrameTest {
    private STEDWindow stedWindow;

    public FontMapperDesktopFrameTest() {
    }

    @Before
    public void testSTEDWindow() {
//        stedWindow = new STEDWindow();
//        stedWindow.load();
    }

    @Test
    public void testFontMapperDesktopFrame() {
        DesktopFrame desktopFrame =
                new DesktopFrame();
        desktopFrame.init();
    }

}
