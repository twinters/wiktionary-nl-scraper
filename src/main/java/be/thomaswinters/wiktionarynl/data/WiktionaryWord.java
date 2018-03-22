package be.thomaswinters.wiktionarynl.data;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WiktionaryWord {
    //    private final Optional<IWiktionaryWord> rootWord;
    private final String word;
    private final Map<WordType, List<WiktionaryDefinition>> definitions;
    private final List<IWiktionaryPage> antonyms;

    public WiktionaryWord(String word, Map<WordType, List<WiktionaryDefinition>> definitions, List<IWiktionaryPage> antonyms) {
//        this.rootWord = rootWord;
        this.word = word;
        this.definitions = ImmutableMap.copyOf(definitions);
        this.antonyms = antonyms;
    }

    public String getWord() {
        return word;
    }

    public Map<WordType, List<WiktionaryDefinition>> getDefinitions() {
        return definitions;
    }


    public List<IWiktionaryPage> getAntonyms() {
        return antonyms;
    }
    
    public String toString() {
        return "WiktionaryWord for " + word + ". Definitions:\n" +
                definitions.entrySet().stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
