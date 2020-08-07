package sted.fontmap

import org.junit.Test

class TestFontMapEntries {
    @Test
    fun testAdd() {
        var i = 1
        val entries = FontMapEntries()
        val entry1 = FontMapEntry("a", "b")
        entry1.isBeginsWith = true
        entries.add(entry1)
        assert(entries.size() == i++)
        val entry2 = FontMapEntry("a", "b")
        entry2.isEndsWith = true
        entries.add(entry2)
        assert(entries.size() == i++)
        val entry3 = FontMapEntry("a", "b")
        entry3.isBeginsWith = true
        entry3.isEndsWith = true
        entries.add(entry3)
        assert(entries.size() == i++)
        val entry4 = FontMapEntry("a", "b")
        entry4.followedBy = "a"
        entries.add(entry4)
        assert(entries.size() == i++)
        val entry5 = FontMapEntry("a", "b")
        entry5.precededBy = "a"
        entries.add(entry5)
        assert(entries.size() == i++)
        val entry6 = FontMapEntry("a", "b")
        entry6.followedBy = "a"
        entry6.precededBy = "a"
        entries.add(entry6)
        assert(entries.size() == i++)
        val entry7 = FontMapEntry("a", "b")
        entry7.isBeginsWith = true
        entry7.followedBy = "a"
        entry7.precededBy = "a"
        entries.add(entry7)
        assert(entries.size() == i++)
        val entry8 = FontMapEntry("a", "b")
        entry8.isBeginsWith = true
        entry8.isEndsWith = true
        entry8.followedBy = "a"
        entry8.precededBy = "a"
        entries.add(entry8)
        assert(entries.size() == i++)
        val entry9 = FontMapEntry("a", "b")
        entries.add(entry9)
        assert(entries.size() == i)
    }
}