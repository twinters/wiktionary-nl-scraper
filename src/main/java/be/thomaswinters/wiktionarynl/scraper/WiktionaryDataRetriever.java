package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.WiktionaryDefinition;
import be.thomaswinters.wiktionarynl.data.WiktionaryWord;
import be.thomaswinters.wiktionarynl.data.WordType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static be.thomaswinters.wiktionarynl.data.WordType.*;

public class WiktionaryDataRetriever {

    private static final String BASE_URL = "https://nl.wiktionary.org/wiki/";

    private static final Map<String, WordType> WORDTYPE_TITLES;

    static {
        ImmutableMap.Builder<String, WordType> b = ImmutableMap.builder();
        b.put("WikiWoordenboek:Zelfstandig naamwoord", NOUN);
        b.put("WikiWoordenboek:Werkwoord", VERB);
        b.put("WikiWoordenboek:Bijvoeglijk naamwoord", ADJECTIVE);
        WORDTYPE_TITLES = b.build();
    }


    private Cache<String, List<IWiktionaryWord>> definitionCache = CacheBuilder.newBuilder().maximumSize(1000).build();

    public List<IWiktionaryWord> retrieveDefinitions(String word) throws IOException, ExecutionException {
        return definitionCache.get(word, () -> {

            List<IWiktionaryWord> result = new ArrayList<>();
            Document doc;
            try {
                doc = Jsoup.connect(BASE_URL + word).get();
            } catch (HttpStatusException e) {
                return result;
            }

            // Find a defition, and make the list only use uniques
            List<Element> wordtypeTitles = doc.getElementsByAttribute("title").stream().filter(e -> WORDTYPE_TITLES.keySet().contains(e.attr("title"))).distinct().collect(Collectors.toList());

//        List<Element> nounLinkStart = doc.getElementsByAttributeValue("title", WORDTYPE_TITLES.get(NOUN)).stream().distinct().collect(Collectors.toList());

            for (Element element : wordtypeTitles) {// Iterate until we find the nounHeaderElement
                Optional<Element> nounHeaderElement = element.parents().stream().filter(this::isHeader).findFirst();
                if (nounHeaderElement.isPresent()) {
                    Element definitionsList = findNextList(nounHeaderElement.get());

                    List<WiktionaryDefinition> definitions = definitionsList.children().stream().map(this::getDefition).collect(Collectors.toList());

                    // TODO: make proxy!
                    Optional<IWiktionaryWord> rootWord = rootWordFinder.getRootWord(this, word, definitions);
                    List<IWiktionaryWord> antonyms = retrieveAntonyms();
                    result.add(new WiktionaryWord(WORDTYPE_TITLES.get(element.attr("title")), rootWord, word, definitions, antonyms));
                }
            }


            return result;
        });
    }

    private List<IWiktionaryWord> retrieveAntonyms() {
        return new ArrayList<>();
    }

    private RootWordFinder rootWordFinder = new RootWordFinder();

    private WiktionaryDefinition getDefition(Element li) {
        String text = li.text();

        // Remove dl and dd
        List<String> toRemoveElements = li.children().select("*").stream().filter(e -> e.tagName()
                .equals("dl"))
                .flatMap(dl -> dl.children().select("*").stream())
                .flatMap(dl -> dl.textNodes().stream()
                        .map(textnode -> textnode.getWholeText())).collect(Collectors.toList());
        toRemoveElements.sort((e, f) -> f.length() - e.length());
        for (String string : toRemoveElements) {
            if (string.length() > 2) {
                text = text.replaceAll(Pattern.quote(string), "");
            }
        }

        Optional<String> category = Optional.empty();

        // Check if a category is specified
        int firstClosingBracket = text.indexOf(")");
        int firstOpeningBracket = text.indexOf("(");
        if (firstOpeningBracket == 0 && firstClosingBracket > firstOpeningBracket) {
            String categoryText = text.substring(firstOpeningBracket + 1, firstClosingBracket);
            category = Optional.of(categoryText);
            text = text.substring(firstClosingBracket + 1).trim();
        }

        return new WiktionaryDefinition(category, text.trim());
    }

    private Element findNextList(Element e) {
        Element current = e;
        while (!current.tag().getName().equals("ol") && !current.tag().getName().equals("ul")) {
            current = current.nextElementSibling();
        }
        return current;
    }


    private boolean isHeader(Element e) {
        return e.tag().getName().equals("h3") || e.tag().getName().equals("h4") || e.tag().getName().equals("h5");
    }

    public static void main(String[] args) throws IOException, ExecutionException {

        System.out.println(new WiktionaryDataRetriever().retrieveDefinitions("massamoordenaar"));
    }
}