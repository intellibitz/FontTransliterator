package sted.fontmap;

import java.awt.*;

/**
 * Created by IntelliJ IDEA. User: sara Date: May 18, 2007 Time: 2:10:05 PM To
 * change this template use File | Settings | File Templates.
 */
public class FontInfo {
    private Font font;
    private String path;

    public FontInfo() {
    }

    public FontInfo(Font font, String path) {
        this.font = font;
        this.path = path;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FontInfo fontInfo = (FontInfo) o;

        if (!font.equals(fontInfo.font)) {
            return false;
        }
        if (!path.equals(fontInfo.path)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = font.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
