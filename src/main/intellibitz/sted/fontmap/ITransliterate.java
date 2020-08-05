package intellibitz.sted.fontmap;

import java.util.List;


public interface ITransliterate {
    String parseLine(String input);

    void setReverseTransliterate(boolean flag);

    void setHTMLAware(boolean flag);

    void setEntries(ITransliterate.IEntries entries);

    public interface IEntries {
        IEntry getReverseMapping(String wordToConvert);

        IEntry getDirectMapping(String wordToConvert);

        List<FontMapEntry> isRuleFound(String word);

        boolean isInWord1(String word);

        boolean isInWord2(String word);
    }

    public interface IEntry {
        String getFrom();

        String getTo();

    }
}
