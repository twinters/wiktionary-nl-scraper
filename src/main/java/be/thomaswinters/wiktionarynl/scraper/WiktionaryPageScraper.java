package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import be.thomaswinters.wiktionarynl.util.LanguagePool;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class WiktionaryPageScraper {

    private static final String BASE_URL = "https://nl.wiktionary.org/wiki/";


    private final Cache<String, List<IWiktionaryWord>> definitionCache = CacheBuilder.newBuilder().maximumSize(1000).build();
    private final LanguagePool languagePool = new LanguagePool();
    private final DefinitionsRetriever definitionFinder = new DefinitionsRetriever();
    private final AntonymRetriever antonymFinder = new AntonymRetriever();

    public static void main(String[] args) throws IOException, ExecutionException {

        System.out.println(new WiktionaryPageScraper().retrieveDefinitions("mooi"));
    }

    public List<IWiktionaryWord> retrieveDefinitions(String word) throws IOException, ExecutionException {
        return definitionCache.get(word, () -> {
            List<IWiktionaryWord> result = new ArrayList<>();
            Document doc;
            try {
                doc = Jsoup.connect(BASE_URL + word).get();
            } catch (HttpStatusException e) {
                return result;
            }

            Element content = doc.getElementById("mw-content-text").getElementsByClass("mw-parser-output").get(0);
            Map<Language, Elements> languageParts = getLanguageParts(content);

            System.out.println(languageParts);

            Map<WordType, List<WiktionaryDefinition>> definitions = definitionFinder.retrieveDefinitions(doc);

            // TODO: make proxy!
            List<IWiktionaryPage> antonyms = antonymFinder.retrieveAntonyms(null);
            result.add(new WiktionaryWord(word, definitions, antonyms));

            return result;
        });
    }

    private Map<Language, Elements> getLanguageParts(Element content) {

        Map<Language, Elements> result = new HashMap<>();

        Optional<Language> currentLanguage = Optional.empty();
        List<Element> currentRelevantElements = new ArrayList<>();
        for (Element e : content.children()) {
            if (e.id().equals("toc")) {
                // Table of content
                continue;
            }

            // Marker for new language
            if (e.tag().getName().equals("h2")) {
                if (!currentLanguage.isPresent() && !currentRelevantElements.isEmpty()) {
                    // No language present, but there are elements
                    System.out.println("WARNING: The following elements got lost due to no present language: " + currentRelevantElements);
                } else {
                    if (currentLanguage.isPresent()) {
                        result.put(currentLanguage.get(), new Elements(currentRelevantElements));
                    }
                    currentLanguage = Optional.of(languagePool.createLanguage(e.text()));
                    currentRelevantElements = new ArrayList<>();
                }
            }
            currentRelevantElements.add(e);
        }
        if (currentLanguage.isPresent())

        {
            result.put(currentLanguage.get(), new Elements(currentRelevantElements));
        }

        return result;
    }
}
