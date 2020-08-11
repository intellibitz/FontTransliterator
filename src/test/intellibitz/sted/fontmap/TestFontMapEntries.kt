package sted.fontmap

import kotlin.test.Test

class TestFontMapEntries {
    @Test
    fun testAdd() {
        var i = 1
        val entries = FontMapEntries()
        val entry1 = FontMapEntry()
        entry1.init("a", "b")
        val entry2 = FontMapEntry()
        entry2.init("a", "b")
        val entry3 = FontMapEntry()
        entry3.init("a", "b")
        val entry4 = FontMapEntry()
        entry4.init("a", "b")
        val entry5 = FontMapEntry()
        entry5.init("a", "b")
        val entry6 = FontMapEntry()
        entry6.init("a", "b")
        val entry7 = FontMapEntry()
        entry7.init("a", "b")
        val entry8 = FontMapEntry()
        entry8.init("a", "b")
        val entry9 = FontMapEntry()
        entry9.init("a", "b")
        entry1.isBeginsWith = true
        entries.add(entry1)
        assert(entries.size() == i++)
        entry2.isEndsWith = true
        entries.add(entry2)
        assert(entries.size() == i++)
        entry3.isBeginsWith = true
        entry3.isEndsWith = true
        entries.add(entry3)
        assert(entries.size() == i++)
        entry4.followedBy = "a"
        entries.add(entry4)
        assert(entries.size() == i++)
        entry5.precededBy = "a"
        entries.add(entry5)
        assert(entries.size() == i++)
        entry6.followedBy = "a"
        entry6.precededBy = "a"
        entries.add(entry6)
        assert(entries.size() == i++)
        entry7.isBeginsWith = true
        entry7.followedBy = "a"
        entry7.precededBy = "a"
        entries.add(entry7)
        assert(entries.size() == i++)
        entry8.isBeginsWith = true
        entry8.isEndsWith = true
        entry8.followedBy = "a"
        entry8.precededBy = "a"
        entries.add(entry8)
        assert(entries.size() == i++)
        entries.add(entry9)
        assert(entries.size() == i)
    }
}