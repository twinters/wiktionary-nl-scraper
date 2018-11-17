package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.function.Supplier;

public class WiktionaryWordProxy implements IWiktionaryWord {
    private IWiktionaryWord loadedWord = null;
    private Supplier<IWiktionaryWord> loader;

    public WiktionaryWordProxy(Supplier<IWiktionaryWord> loader) {
        this.loader = loader;
    }

    private IWiktionaryWord getWiktionaryWord() {
        if (loadedWord != null) {
            return loadedWord;
        }
        this.loadedWord = loader.get();
        if (this.loadedWord == null) {
            throw new IllegalStateException("Loaded a null loadedWord. " + loader);
        }
        return loadedWord;
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
