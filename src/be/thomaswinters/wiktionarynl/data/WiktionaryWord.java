package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.stream.Collectors;

public class WiktionaryWord {
    private final String word;
    private final List<WiktionaryDefinition> definition;

    public WiktionaryWord(String word, List<WiktionaryDefinition> definition) {
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public List<WiktionaryDefinition> getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return "WiktionaryWord for " + word + ". Definitions:\n" +
                definition.stream().map(e -> e.toString()).collect(Collectors.joining("\n"));
    }
}
