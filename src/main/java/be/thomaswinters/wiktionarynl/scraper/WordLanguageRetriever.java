package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;

public class WordLanguageRetriever {
    private final DefinitionsRetriever definitionFinder = new DefinitionsRetriever();
    private final AntonymRetriever antonymFinder = new AntonymRetriever();

    public IWiktionaryWord scrapeWord(String word, Elements elements) {
        Map<WordType, List<WiktionaryDefinition>> definitions = definitionFinder.retrieveDefinitions(elements);

        // TODO: make proxy!
        List<IWiktionaryPage> antonyms = antonymFinder.retrieveAntonyms(null);

        return new WiktionaryWord(word, definitions, antonyms);
    }
}
