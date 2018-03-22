package be.thomaswinters.wiktionarynl.data;

import java.util.Collection;
import java.util.function.Supplier;

public class WiktionaryPageProxy implements IWiktionaryPage {
    private IWiktionaryPage page = null;
    private Supplier<IWiktionaryPage> loader;

    public WiktionaryPageProxy(Supplier<IWiktionaryPage> loader) {
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
    public WiktionaryWord getWord(Language language) {
        return getWiktionaryPage().getWord(language);
    }
}
