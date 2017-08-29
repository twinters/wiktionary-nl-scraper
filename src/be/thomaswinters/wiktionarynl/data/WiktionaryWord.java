package be.thomaswinters.wiktionarynl.data;

import java.util.List;

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
}
