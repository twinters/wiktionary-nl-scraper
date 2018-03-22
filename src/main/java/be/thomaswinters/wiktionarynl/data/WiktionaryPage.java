package be.thomaswinters.wiktionarynl.data;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;

public class WiktionaryPage implements IWiktionaryPage {
    private final Map<Language, WiktionaryWord> languageMap;

    public WiktionaryPage(Map<Language, WiktionaryWord> languageMap) {
        this.languageMap = ImmutableMap.copyOf(languageMap);
    }

    @Override
    public Collection<Language> getLanguages() {
        return languageMap.keySet();
    }

    @Override
    public WiktionaryWord getWord(Language language) {
        return languageMap.get(language);
    }
}
