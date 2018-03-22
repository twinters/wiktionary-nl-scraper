package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WiktionaryDataRetriever {

    private static final String BASE_URL = "https://nl.wiktionary.org/wiki/";


    private final Cache<String, List<IWiktionaryWord>> definitionCache = CacheBuilder.newBuilder().maximumSize(1000).build();
    private final DefinitionsFinder definitionFinder = new DefinitionsFinder();
    private final AntonymFinder antonymFinder = new AntonymFinder();

    public List<IWiktionaryWord> retrieveDefinitions(String word) throws IOException, ExecutionException {
        return definitionCache.get(word, () -> {
            List<IWiktionaryWord> result = new ArrayList<>();
            Document doc;
            try {
                doc = Jsoup.connect(BASE_URL + word).get();
            } catch (HttpStatusException e) {
                return result;
            }

            Map<WordType, List<WiktionaryDefinition>> definitions = definitionFinder.retrieveDefinitions(doc);

            // TODO: make proxy!
            List<IWiktionaryPage> antonyms = antonymFinder.retrieveAntonyms(null);
            result.add(new WiktionaryWord(word, definitions, antonyms));

            return result;
        });
    }

    public static void main(String[] args) throws IOException, ExecutionException {

        System.out.println(new WiktionaryDataRetriever().retrieveDefinitions("massamoordenaar"));
    }
}
