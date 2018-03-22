package be.thomaswinters.wiktionarynl.data;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;

public class WiktionaryPage implements IWiktionaryPage {
    private final Map<Language, IWiktionaryWord> languageMap;

    public WiktionaryPage(Map<Language, IWiktionaryWord> languageMap) {
        this.languageMap = ImmutableMap.copyOf(languageMap);
    }

    @Override
    public Collection<Language> getLanguages() {
        return languageMap.keySet();
    }

    @Override
    public IWiktionaryWord getWord(Language language) {
        return languageMap.get(language);
    }
}
