package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryPage;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AntonymRetriever {
    private static final List<String> antonymTags = Arrays.asList("antoniem");

    public List<IWiktionaryPage> retrieveAntonyms(Map<String, Elements> elements) {
        return null;

    }
}
