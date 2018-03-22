package be.thomaswinters.wiktionarynl.util;

import be.thomaswinters.wiktionarynl.data.Language;

import java.util.HashMap;
import java.util.Map;

public class LanguagePool {

    private Map<String, Language> languagePool = new HashMap<>();

    public Language createLanguage(String name) {
        if (!languagePool.containsKey(name)) {
            languagePool.put(name, new Language(name));
        }
        return languagePool.get(name);
    }


}
