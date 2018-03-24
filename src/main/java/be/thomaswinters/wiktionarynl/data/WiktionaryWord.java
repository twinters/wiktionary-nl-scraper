package be.thomaswinters.wiktionarynl.data;

import java.util.List;

public class WiktionaryWord implements IWiktionaryWord {
    private final String word;
    private final DefinitionList definitions;
    private final List<IWiktionaryWord> antonyms;

    public WiktionaryWord(String word, DefinitionList definitions, List<IWiktionaryWord> antonyms) {
        this.word = word;
        this.definitions = definitions;
        this.antonyms = antonyms;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public DefinitionList getDefinitions() {
        return definitions;
    }

    @Override
    public List<IWiktionaryWord> getAntonyms() {
        return antonyms;
    }

    @Override
    public String toString() {
        return "WiktionaryWord{" +
                "word='" + word + '\'' +
                ", definitions=" + definitions +
                ", antonyms=" + antonyms +
                '}';
    }
}
