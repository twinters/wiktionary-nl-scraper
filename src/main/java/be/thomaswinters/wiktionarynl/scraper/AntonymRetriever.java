package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.Language;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AntonymRetriever {
    private static final List<String> antonymTags = Arrays.asList("antoniem");

    private final IWiktionaryWordScraper scraper;

    public AntonymRetriever(IWiktionaryWordScraper scraper) {
        this.scraper = scraper;
    }

    public List<IWiktionaryWord> retrieveAntonyms(Language language, Map<String, Elements> elements) {

        Stream<Elements> qualifiedElements = elements.entrySet().stream()
                .filter(entry -> antonymTags.contains(entry.getKey()))
                .map(entry -> entry.getValue());


        return qualifiedElements
                .flatMap(e -> e.select("ul li a").stream())
                .map(a -> a.text())
                .map(word -> scraper.scrape(language, word))
                .collect(Collectors.toList());

    }
}
