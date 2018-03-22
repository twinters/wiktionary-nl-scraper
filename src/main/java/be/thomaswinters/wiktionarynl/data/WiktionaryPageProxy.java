package be.thomaswinters.wiktionarynl.data;

import java.util.Collection;
import java.util.function.Supplier;

public class WiktionaryPageProxy implements IWiktionaryPage {
    private WiktionaryPage page = null;
    private Supplier<WiktionaryPage> loader;

    public WiktionaryPageProxy(Supplier<WiktionaryPage> loader) {
        this.loader = loader;
    }

    private IWiktionaryPage getWiktionaryPage() {
        if (page != null) {
            return page;
        }
        this.page = loader.get();
        return page;
    }

    @Override
    public Collection<Language> getLanguages() {
        return page.getLanguages();
    }

    @Override
    public IWiktionaryWord getWord(Language language) {
        return page.getWord(language);
    }
}
