package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.function.Supplier;

public class WiktionaryWordProxy implements IWiktionaryWord {
    private IWiktionaryWord word = null;
    private Supplier<IWiktionaryWord> loader;

    public WiktionaryWordProxy(Supplier<IWiktionaryWord> loader) {
        this.loader = loader;
    }

    private IWiktionaryWord getWiktionaryWord() {
        if (word != null) {
            return word;
        }
        this.word = loader.get();
        if (this.word == null) {
            throw new IllegalStateException("Loaded a null word. " + loader);
        }
        return word;
    }

    @Override
    public String getWord() {
        return getWiktionaryWord().getWord();
    }

    @Override
    public DefinitionList getDefinitions() {
        return getWiktionaryWord().getDefinitions();
    }

    @Override
    public List<IWiktionaryWord> getAntonyms() {
        return getWiktionaryWord().getAntonyms();
    }
}
