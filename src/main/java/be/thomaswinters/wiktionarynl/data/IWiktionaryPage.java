package be.thomaswinters.wiktionarynl.data;

import java.util.Collection;

public interface IWiktionaryPage {
    Collection<Language> getLanguages();

    WiktionaryWord getWord(Language language);
}
