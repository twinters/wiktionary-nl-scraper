package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class WiktionaryWordProxy implements IWiktionaryWord {
    private IWiktionaryWord word = null;
    private Function<Void, IWiktionaryWord> loader;

    public WiktionaryWordProxy(Function<Void, IWiktionaryWord> loader) {
        this.loader = loader;
    }

    private IWiktionaryWord getWiktionaryWord() {
        if (word != null) {
            return word;
        }

        this.word = loader.apply(null);

        return word;

    }

    @Override
    public String getWord() {
        return getWiktionaryWord().getWord();
    }

    @Override
    public List<WiktionaryDefinition> getDefinitions() {
        return getWiktionaryWord().getDefinitions();
    }

    @Override
    public WordType getWordType() {
        return getWiktionaryWord().getWordType();
    }

    @Override
    public Optional<IWiktionaryWord> getRootWord() {
        return getWiktionaryWord().getRootWord();
    }

    @Override
    public IWiktionaryWord getTotalRootWord() {
        return getWiktionaryWord().getTotalRootWord();
    }
}
