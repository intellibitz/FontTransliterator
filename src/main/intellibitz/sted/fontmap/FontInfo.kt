package sted.fontmap

import java.awt.Font

class FontInfo(var font: Font, var path: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val fontInfo = other as FontInfo
        if (font != fontInfo.font) {
            return false
        }
        return path == fontInfo.path
    }

    override fun hashCode(): Int {
        var result: Int = font.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}