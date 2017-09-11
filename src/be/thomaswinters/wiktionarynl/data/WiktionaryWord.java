package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WiktionaryWord {
    private final WordType wordType;
    private final Optional<WiktionaryWord> rootWord;
    private final String word;
    private final List<WiktionaryDefinition> definition;

    public WiktionaryWord(WordType wordType, Optional<WiktionaryWord> rootWord, String word, List<WiktionaryDefinition> definition) {
        this.wordType = wordType;
        this.rootWord = rootWord;
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public List<WiktionaryDefinition> getDefinitions() {
        return definition;
    }

    public WordType getWordType() {
        return wordType;
    }

    public Optional<WiktionaryWord> getRootWord() {
        return rootWord;
    }

    public WiktionaryWord getTotalRootWord() {
        WiktionaryWord totalRoot = this;
        while (totalRoot.getRootWord().isPresent()) {
            totalRoot = totalRoot.getRootWord().get();
        }
        return totalRoot;
    }

    @Override
    public String toString() {
        return "WiktionaryWord for " + word + ". Definitions:\n" +
                definition.stream().map(WiktionaryDefinition::toString).collect(Collectors.joining("\n"));
    }
}
