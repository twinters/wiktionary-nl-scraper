package be.thomaswinters.wiktionarynl.data;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WiktionaryWord implements IWiktionaryWord {
    private final String word;
    private final Map<WordType, List<WiktionaryDefinition>> definitions;
    private final List<IWiktionaryWord> antonyms;

    public WiktionaryWord(String word, Map<WordType, List<WiktionaryDefinition>> definitions, List<IWiktionaryWord> antonyms) {
        this.word = word;
        this.definitions = ImmutableMap.copyOf(definitions);
        this.antonyms = antonyms;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public Map<WordType, List<WiktionaryDefinition>> getDefinitions() {
        return definitions;
    }

    @Override
    public List<IWiktionaryWord> getAntonyms() {
        return antonyms;
    }

    public String toString() {
        return "WiktionaryWord for " + word + ". Definitions:\n" +
                definitions.entrySet().stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
