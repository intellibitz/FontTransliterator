package sted.fontmap

import org.junit.Test

class TestFontMapEntry {
    @Test
    fun testCompareTo1() {
        val entry1 = FontMapEntry("a", "b")
        val entry2 = FontMapEntry("a", "b")
        assert(entry1.compareTo(entry2) == 0)
        entry1.isBeginsWith = true
        assert(entry1.compareTo(entry2) != 0)
        entry2.isBeginsWith = true
        assert(entry1.compareTo(entry2) == 0)
        entry2.isEndsWith = true
        assert(entry1.compareTo(entry2) != 0)
        entry1.isEndsWith = true
        assert(entry1.compareTo(entry2) == 0)
        entry1.precededBy = "a"
        assert(entry1.compareTo(entry2) != 0)
        entry2.precededBy = "a"
        assert(entry1.compareTo(entry2) == 0)
        entry1.followedBy = "a"
        assert(entry1.compareTo(entry2) != 0)
        entry2.followedBy = "a"
        assert(entry1.compareTo(entry2) == 0)
    }

    @Test
    fun testCompareTo2() {
        val entry1 = FontMapEntry("a", "b")
        val entry2 = FontMapEntry("a", "b")
        entry1.precededBy = "a"
        assert(entry1.compareTo(entry2) != 0)
        entry2.precededBy = "a"
        assert(entry1.compareTo(entry2) == 0)
        entry1.followedBy = "a"
        assert(entry1.compareTo(entry2) != 0)
        entry1.isBeginsWith = true
        assert(entry1.compareTo(entry2) != 0)
        entry1.isEndsWith = true
        assert(entry1.compareTo(entry2) != 0)
    }

    @Test
    fun testEquals() {
        val entry1 = FontMapEntry("a", "b")
        val entry2 = FontMapEntry("a", "b")
        assert(entry1 == entry2)
        entry1.precededBy = "a"
        assert(entry1 != entry2)
        entry2.precededBy = "a"
        assert(entry1 == entry2)
    }
}