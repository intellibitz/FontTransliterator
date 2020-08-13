package sted.fontmap

import sted.fontmap.ITransliterate.IEntries
import java.util.*

class DefaultTransliterator : ITransliterate {
    private var reverseTransliterate = false
    private var isHTMLAware = true
    private var isParseMode = true
    private lateinit var iEntries: IEntries
    private var converted = 0
    private var prevWord = ""
    override fun setEntries(entries: IEntries) {
        this.iEntries = entries
    }

    override fun setReverseTransliterate(flag: Boolean) {
        reverseTransliterate = flag
    }

    override fun setHTMLAware(flag: Boolean) {
        isHTMLAware = flag
    }

    override fun parseLine(input: String): String {
        val output = StringBuffer()
        val stringTokenizer = StringTokenizer(input, " ", true)
        while (stringTokenizer.hasMoreTokens()) {
            val word = stringTokenizer.nextToken()
            // break the word into more word, for HTML parsing
            val st = StringTokenizer(
                word,
                "<" +
                        ">"
                        +
                        "&" +
                        ";"
                        +
                        " ",
                true
            )
            while (st.hasMoreTokens()) {
                val currWord = st.nextToken()
                parseWord(currWord, prevWord, output)
                prevWord = currWord
            }
        }
        return output.toString()
    }

    private fun parseWord(word: String, prevWord: String, output: StringBuffer) {
        // if its a space, do not parse
        if (" " == word) {
            output.append(word)
            return
        }
        val b = ">" == word || ";" == word
        if (("<" == word || "&" == word)
            && isHTMLAware
        ) {
            isParseMode = false
            output.append(word)
            return
        } else if (b
            && isHTMLAware
        ) {
            isParseMode = ";" != word ||
                    "lt" != prevWord
            output.append(word)
            return
        }
        // if its not in parse mode, skip parsing, unless an html end character is hit
        if (!b
            && !isParseMode
        ) {
            output.append(word)
            return
        }
        converted = 0
        convertWord(
            word, output, word, word.length, "",
            "", word
        )
    }

    private fun convertWord(
        word: String, output: StringBuffer, chopped: String,
        wordLen: Int, translated: String, leftover: String,
        original: String
    ) {
        var converted = translated
        var remains = leftover
        if (this.converted == wordLen) {
            return
        }
        // translate the whole word, in one shot, if possible
        if (translate(word, output, converted, remains, original)) {
            this.converted += word.length
        } else {
            var remaining = word
            // chop the last char from the word, and try the remaining word first
            if (word.length > 1) {
                remaining = word.substring(0, word.length - 1)
                // get the last char
                remains = word.substring(word.length - 1) +
                        remains
            }
            convertWord(
                remaining, output, chopped, wordLen, converted,
                remains, original
            )
            converted += remaining
            // now try the leftover remaining word
            val remaining2 = chopped.substring(remaining.length)
            remains = ""
            convertWord(
                remaining2, output, remaining2, wordLen, converted,
                remains, original
            )
        }
    }

    private fun translate(
        word: String, output: StringBuffer,
        translated: String,
        leftover: String, original: String
    ): Boolean {
        val result: CharArray? = if (isParseMode) {
            translateWord(word, translated, leftover, original)
        } else {
            word.toCharArray()
        }
        if (result != null) {
            output.append(result)
            return true
        }
        return false
    }

    /**
     * for forward translate looks in the values , for backward translate looks
     * in the keyset
     *
     * @param word is the _inputFileSelectorPanel character word
     * @return char[] the translated character word
     */
    private fun translateWord(
        word: String, translated: String,
        leftover: String,
        original: String
    ): CharArray? {
        val wordToConvert = applyIndirectMappingIfAny(word, translated, leftover, original)
        var chars: CharArray? = null
        // if the word is translatable
        if (isWordMapped(wordToConvert)) {
            chars = if (reverseTransliterate) {
                val entry = iEntries.getReverseMapping(wordToConvert)
                entry?.from?.toCharArray() ?: wordToConvert.toCharArray()
            } else {
                val entry = iEntries.getDirectMapping(wordToConvert)
                entry?.to?.toCharArray() ?: wordToConvert.toCharArray()
            }
        }
        // if only 1 character and still not mapped, then is not in language, so return the char
        // if not return, coz the word can be broken into letters and then try to map again
        if (chars == null && wordToConvert.length == 1) {
            chars = charArrayOf(wordToConvert[0])
        }
        return chars
    }

    private fun applyIndirectMappingIfAny(
        word: String, translated: String,
        leftover: String, template: String
    ): String {
        var result = word
        var list = iEntries.isRuleFound(word)
        // if rule fontMap.getEntries () are found for the word
        if (list.isNotEmpty()) {
            val iterator = list.iterator()
            result = word
            while (iterator.hasNext()) {
                val entry = iterator.next()
                result = indirectMap(
                    entry, result, translated, leftover,
                    template
                )
            }
        } else if (iEntries.isInWord1(word)) {
            var len = word.length
            while (len > 0) {
                val nword = result.substring(0, len--)
                list = iEntries.isRuleFound(nword)
                // if rule entries are found for the word
                if (list.isNotEmpty()) {
                    val iterator = list.iterator()
                    result = word
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        if (shouldBeginsWithIndirectMappingApplied(
                                entry,
                                translated,
                                template
                            )
                            || shouldPrecededByIndirectMappingApplied(
                                entry,
                                translated
                            )
                        ) {
                            result = indirectMap(
                                entry, result, translated,
                                leftover, template
                            )
                        }
                    }
                }
            }
        }
        return result
    }

    private fun isWordMapped(word: String): Boolean {
        return if (reverseTransliterate) {
            iEntries.isInWord2(word)
        } else iEntries.isInWord1(word)
    }

    companion object {
        private fun indirectMap(
            entry: FontMapEntry?, word: String,
            translated: String?, leftover: String,
            template: String
        ): String {
            var result = word
            if (shouldBeginsWithIndirectMappingApplied(entry, translated, template)) {
                // if the mapping is already present, then nothing to replace
                if (!template.startsWith(entry!!.to)) {
                    result = word.replaceFirst(entry.from.toRegex(), entry.to)
                }
            }
            if (entry!!.isEndsWith && translated != null && template.length -
                translated.length ==
                1
            ) {
                // if the mapping is already present, then nothing to replace
                if (!template.endsWith(entry.to)) {
                    result = word.replaceFirst(entry.from.toRegex(), entry.to)
                }
            }
            if (entry.followedBy != null && entry.followedBy!!.isNotEmpty() &&
                leftover.startsWith(entry.followedBy!!)
            ) {
                // if the mapping is already present, then nothing to replace
                if (word == entry.from ||
                    word.length >= entry.to.length &&
                    word.substring(
                        word.indexOf(entry.from),
                        entry.to.length
                    ) != entry.to
                ) {
                    result = word.replaceFirst(entry.from.toRegex(), entry.to)
                }
            }
            if (shouldPrecededByIndirectMappingApplied(entry, translated)) {
                // if the mapping is already present, then nothing to replace
                if (word == entry.from ||
                    word.length >= entry.to.length &&
                    word.substring(
                        word.indexOf(entry.from),
                        entry.to.length
                    ) != entry.to
                ) {
                    result = word.replaceFirst(entry.from.toRegex(), entry.to)
                }
            }
            return result
        }

        private fun shouldBeginsWithIndirectMappingApplied(
            entry: FontMapEntry?,
            translated: String?,
            template: String
        ): Boolean {
            return (entry!!.isBeginsWith && template.startsWith(entry.from)
                    && translated!!.isEmpty())
        }

        private fun shouldPrecededByIndirectMappingApplied(entry: FontMapEntry?, translated: String?): Boolean {
            return (entry!!.precededBy != null && entry.precededBy!!.isNotEmpty()
                    &&
                    translated != null &&
                    translated.endsWith(entry.precededBy!!))
        }
    }
}