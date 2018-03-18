package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WiktionaryWord implements IWiktionaryWord {
    private final WordType wordType;
    private final Optional<IWiktionaryWord> rootWord;
    private final String word;
    private final List<WiktionaryDefinition> definition;

    public WiktionaryWord(WordType wordType, Optional<IWiktionaryWord> rootWord, String word, List<WiktionaryDefinition> definition) {
        this.wordType = wordType;
        this.rootWord = rootWord;
        this.word = word;
        this.definition = definition;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public List<WiktionaryDefinition> getDefinitions() {
        return definition;
    }

    @Override
    public WordType getWordType() {
        return wordType;
    }

    @Override
    public Optional<IWiktionaryWord> getRootWord() {
        return rootWord;
    }

    @Override
    public IWiktionaryWord getTotalRootWord() {
        IWiktionaryWord totalRoot = this;
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
