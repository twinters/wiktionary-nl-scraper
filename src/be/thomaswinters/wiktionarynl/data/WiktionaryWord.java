package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.stream.Collectors;

public class WiktionaryWord {
    private final WordType wordType;
    private final String word;
    private final List<WiktionaryDefinition> definition;

    public WiktionaryWord(WordType wordType, String word, List<WiktionaryDefinition> definition) {
        this.wordType = wordType;
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public List<WiktionaryDefinition> getDefinition() {
        return definition;
    }

    public WordType getWordType() {
        return wordType;
    }

    @Override
    public String toString() {
        return "WiktionaryWord for " + word + ". Definitions:\n" +
                definition.stream().map(WiktionaryDefinition::toString).collect(Collectors.joining("\n"));
    }
}
