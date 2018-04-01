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

public class WiktionaryPageScraper implements IWiktionaryWordScraper {


    private final Cache<String, WiktionaryPage> definitionCache = CacheBuilder.newBuilder().softValues().build();

    private final LanguagePool languagePool = new LanguagePool();
    private final WordLanguageRetriever wordLanguageRetriever = new WordLanguageRetriever(this);


    private final String languageCode;


    public WiktionaryPageScraper(String languageCode) {
        this.languageCode = languageCode;
    }

    public WiktionaryPageScraper() {
        this("nl");
    }


    private String getWiktionaryUrl(String word) {
        return "https://" + languageCode + ".wiktionary.org/wiki/" + word;
    }


    public WiktionaryPage scrapePage(String word) throws IOException, ExecutionException, HttpStatusException {
        if (word == null) {
            throw new IllegalArgumentException("Input word can't be null");
        }
        return definitionCache.get(word, () -> {
            Map<Language, WiktionaryWord> pageElements = new HashMap<>();
            try {
                Document doc = Jsoup.connect(getWiktionaryUrl(word)).timeout(10 * 1000).get();
                Element content = doc.getElementById("mw-content-text").getElementsByClass("mw-parser-output").get(0);
                Map<Language, Elements> languageParts = getLanguageParts(content);

                for (Map.Entry<Language, Elements> entry : languageParts.entrySet()) {
                    pageElements.put(entry.getKey(), wordLanguageRetriever.scrapeWord(word, entry.getKey(), entry.getValue()));
                }
            } catch (HttpStatusException httpEx) {
                System.out.println("couldn't find " + word + ". Exception: " + httpEx.getUrl() + " / " + httpEx.getStatusCode() + " / " + httpEx.getMessage());
            }

            return new WiktionaryPage(pageElements);
        });
    }

    /**
     * Takes all the h2 of the element as the name of the language, and all other elements as part of the last
     * encountered language definition element.
     *
     * @param content
     * @return
     */
    private Map<Language, Elements> getLanguageParts(Element content) {
        Map<Language, Elements> allLanguageElements = new HashMap<>();

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
//                    System.out.println("WARNING: The following elements got lost due to no present language: " + currentRelevantElements);
                } else {
                    if (currentLanguage.isPresent()) {
                        allLanguageElements.put(currentLanguage.get(), new Elements(currentRelevantElements));
                    }
                }
                currentLanguage = Optional.of(languagePool.createLanguage(e.text()));
                currentRelevantElements = new ArrayList<>();
            } else {
                currentRelevantElements.add(e);
            }
        }
        if (currentLanguage.isPresent()) {
            allLanguageElements.put(currentLanguage.get(), new Elements(currentRelevantElements));
        }

        return allLanguageElements;
    }

    public static void main(String[] args) throws IOException, ExecutionException {

        new WiktionaryPageScraper().scrapePage("mooi");
    }

    String getLanguageCode() {
        return languageCode;
    }

    @Override
    public IWiktionaryWord scrape(Language language, String word) {
        return new WiktionaryWordProxy(() -> {
            try {
                IWiktionaryPage page = scrapePage(word);
                if (page == null || !page.getLanguages().contains(language)) {
                    return new NonExistingWiktionaryWord(word);
                }

                return page.getWord(language);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Could not scrape " + word + " in language " + language, e);
            }
        });
    }
}
