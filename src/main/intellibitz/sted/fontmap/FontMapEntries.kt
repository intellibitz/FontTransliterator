package sted.fontmap

import sted.event.FontMapEntriesChangeEvent
import sted.event.IFontMapEntriesChangeListener
import sted.fontmap.ITransliterate.IEntries
import sted.fontmap.ITransliterate.IEntry
import java.util.*
import javax.swing.event.EventListenerList

/**
 * Created by IntelliJ IDEA. User: Muthu Ramadoss Date: Oct 30, 2003 Time:
 * 12:09:17 AM To change this template use Options | File Templates.
 */
class FontMapEntries : IEntries {
    // unique key
    private val word1: MutableSet<String> = TreeSet()

    // unique value
    private val word2: MutableSet<String> = TreeSet()

    // all words.. key + value.. unique
    private val words: MutableSet<String> = TreeSet()

    // without rules
    private val directEntries: MutableMap<String, FontMapEntry> = TreeMap()

    // map by id.. for faster access to entry
    private val allEntries: MutableMap<String, FontMapEntry> = TreeMap()

    // directEntries with rules
    private val ruleEntries: MutableMap<String, FontMapEntry> = TreeMap()
    val undo = Stack<FontMapEntry>()
    val redo = Stack<FontMapEntry>()
    private val fontMapEntriesChangeListeners = EventListenerList()

    /**
     * Removes all mappings from this TreeMap.
     */
    fun clear() {
        directEntries.clear()
        allEntries.clear()
        ruleEntries.clear()
        word1.clear()
        word2.clear()
        words.clear()
        clearUndoRedo()
    }

    fun clearUndoRedo() {
        undo.clear()
        redo.clear()
    }

    /**
     * Based on the edit, the collection needs to updated
     *
     */
    fun reKey(oldKey: FontMapEntry, fontMapEntry: FontMapEntry) {
        // remove the old entry
        remove(oldKey)
        // add the new edited entry
        addEntry(fontMapEntry)
    }

    fun add(entry: FontMapEntry): Boolean {
        // if valid entry, and entry not already added
        if (isValid(entry)) {
            addEntry(entry)
            return true
        }
        return false
    }

    private fun addEntry(entry: FontMapEntry) {
        val key = entry.from
        val value = entry.to
        if (entry.isRulesSet) {
            // rule entries can have the same key with different set of rules
            ruleEntries[entry.id] = entry
        } else {
            // key must be unique for entries without rules, so store by key
            directEntries[key] = entry
        }
        allEntries[entry.id] = entry
        word1.add(key)
        word2.add(value)
        // add all the words, key + value
        words.add(key)
        words.add(value)
        fireFontMapEntriesChangeEvent()
    }

    fun remove(id: String): FontMapEntry? {
        val entry = allEntries[id]
        if (entry == null) return null
        val fontMapEntry = remove(entry)
        fireFontMapEntriesChangeEvent()
        return fontMapEntry
    }

    private fun remove(entry: FontMapEntry): FontMapEntry? {
        // try rule directEntries first
        var removed = ruleEntries.remove(entry.id)
        // if not try blank directEntries
        if (removed == null) {
            removed = directEntries.remove(entry.from)
        }
        word1.remove(entry.from)
        word2.remove(entry.to)
        // remove from words
        words.remove(entry.from)
        words.remove(entry.to)
        // remove from the master
        allEntries.remove(entry.id)
        fireFontMapEntriesChangeEvent()
        return removed
    }

    fun remove(vals: Collection<FontMapEntry?>): Collection<FontMapEntry?> {
        val iterator = vals.iterator()
        val removedEntries = ArrayList<FontMapEntry?>(vals.size)
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry != null)
                removedEntries.add(remove(entry))
        }
        fireFontMapEntriesChangeEvent()
        return removedEntries
    }

    /**
     * if entry found in ruleEntries, returns true false otherwise
     *
     */
    override fun isRuleFound(word: String): List<FontMapEntry?> {
        val iterator: Iterator<FontMapEntry> = ruleEntries.values.iterator()
        val list: MutableList<FontMapEntry?> = ArrayList()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (word == entry.from) {
                list.add(entry)
            }
        }
        return list
    }

    /**
     * @return true if the entry is valid and can be added to this collection,
     * false otherwise
     */
    fun isValid(entry: FontMapEntry?): Boolean {
        return entry != null && entry.isValid &&
                (entry.isRulesSet || !directEntries.containsKey(entry.from)) &&
                !allEntries
                    .containsValue(
                        entry
                    )
    }

    /**
     * @return true if the entry is valid and can be added to this collection,
     * false otherwise
     */
    fun isValidEdit(entry: FontMapEntry?): Boolean {
        return entry != null && entry.isValid &&
                !allEntries.containsValue(entry)
    }

    override fun getDirectMapping(wordToConvert: String): IEntry? {
        var entry = directEntries[wordToConvert]
        if (entry != null) {
            entry = findDirectMapping(entry, entry)
        }
        return entry
    }

    private fun findDirectMapping(
        entry: FontMapEntry,
        root: FontMapEntry
    ): FontMapEntry {
        val tmp = directEntries[entry.to]
        return if (tmp == null || tmp.to == root.from) {
            entry
        } else {
            findDirectMapping(tmp, root)
        }
    }

    override fun getReverseMapping(wordToConvert: String): IEntry? {
        // match the value for reverse transliterate
        // if value matched, then return the key
        for (fontMapEntry in directEntries.values) {
            var entry = fontMapEntry
            if (entry.to == wordToConvert) {
                entry = findReverseMapping(entry, entry)
                return entry
            }
        }
        return null
    }

    private fun findReverseMapping(
        entry: FontMapEntry,
        root: FontMapEntry
    ): FontMapEntry {
        // match the value for reverse transliterate
        // if value matched, then return the key
        val entries: Iterator<FontMapEntry> = directEntries.values.iterator()
        var tmp: FontMapEntry? = null
        while (entries.hasNext()) {
            val `val` = entries.next()
            if (`val`.to == entry.from) {
                tmp = `val`
                break
            }
        }
        return if (tmp == null || tmp.from == root.to) {
            entry
        } else {
            findReverseMapping(tmp, root)
        }
    }

    override fun isInWord1(word: String): Boolean {
        return word1.contains(word)
    }

    override fun isInWord2(word: String): Boolean {
        return word2.contains(word)
    }

    val allWords: Iterator<String>
        get() = words.iterator()

    fun getWord2(): Iterator<String> {
        return word2.iterator()
    }

    fun values(): Collection<FontMapEntry> {
        return allEntries.values
    }

    fun size(): Int {
        return allEntries.size
    }

    val isEmpty: Boolean
        get() = allEntries.isEmpty()

    /*
    public void addFontMapEntriesChangeListener(
            IFontMapEntriesChangeListener changeListener) {
        fontMapEntriesChangeListeners
                .add(IFontMapEntriesChangeListener.class, changeListener);
    }

    public void removeFontMapEntriesChangeListener(
            IFontMapEntriesChangeListener changeListener) {
        fontMapEntriesChangeListeners
                .remove(IFontMapEntriesChangeListener.class, changeListener);
    }
*/
    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    private fun fireFontMapEntriesChangeEvent() {
        // Guaranteed to return a non-null array
        val listeners = fontMapEntriesChangeListeners.listenerList
        // Process the listeners last to first, notifying
        // those that are interested in this event
        val fontMapEntriesChangeEvent = FontMapEntriesChangeEvent(this)
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === IFontMapEntriesChangeListener::class.java) {
                (listeners[i + 1] as IFontMapEntriesChangeListener)
                    .stateChanged(fontMapEntriesChangeEvent)
            }
            i -= 2
        }
    }
}