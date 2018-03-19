package be.thomaswinters.wiktionarynl;

import be.thomaswinters.wiktionarynl.data.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static be.thomaswinters.wiktionarynl.data.WordType.*;

public class WiktionaryDataRetriever {

    private static final String BASE_URL = "https://nl.wiktionary.org/wiki/";

    private static final Map<String, WordType> WORDTYPE_TITLES;

    static {
        ImmutableMap.Builder b = ImmutableMap.builder();
        b.put("WikiWoordenboek:Zelfstandig naamwoord", NOUN);
        b.put("WikiWoordenboek:Werkwoord", VERB);
        b.put("WikiWoordenboek:Bijvoeglijk naamwoord", ADJECTIVE);
        WORDTYPE_TITLES = b.build();
    }

    ;


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
                    Optional<IWiktionaryWord> rootWord = Optional.empty();
                    if (!definitions.isEmpty()) {
                        // Check if in first child, a link is provided
                    List<IWiktionaryWord> antonyms = retrieveAntonyms();
                    result.add(new WiktionaryWord(WORDTYPE_TITLES.get(element.attr("title")), rootWord, word, definitions, antonyms));
    private List<IWiktionaryWord> retrieveAntonyms() {
        return new ArrayList<>();
    }
//                        Optional<Element> rootLink = definitionsList.children().get(0).children().stream()
//                                .filter(child -> child.text().contains("(")).flatMap(e -> e.getElementsByTag("a").stream())
//                                .filter(e -> e.attr("href").startsWith("/wiki/")).findFirst();

                        Optional<String> possibleRootWord = getRootWord(definitions);

                        if (possibleRootWord.isPresent()) {
                            String newWord = possibleRootWord.get();
                            if (!newWord.equals(word)) {
                                Function<Void, IWiktionaryWord> loader = new Function<Void, IWiktionaryWord>() {
                                    @Override
                                    public IWiktionaryWord apply(Void aVoid) {
                                        try {
                                            List<IWiktionaryWord> rootWords = WiktionaryDataRetriever.this.retrieveDefinitions(newWord);
                                            if (!rootWords.isEmpty()) {
                                                return rootWords.get(0);
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            throw new RuntimeException(ex);
                                        }
                                        throw new RuntimeException("Failed to load rootword");
                                    }
                                };
                                rootWord = Optional.of(new WiktionaryWordProxy(loader));
                            }
                        }
                    }


                    result.add(new WiktionaryWord(WORDTYPE_TITLES.get(element.attr("title")), rootWord, word, definitions));
                }
            }


            return result;
        });
    }

    private List<Pattern> rootFinders = Arrays.asList(
            // Substantief
            Pattern.compile("verkleinwoord enkelvoud van het zelfstandig naamwoord (\\w+)"),

            // Adjectief
            Pattern.compile("verbogen vorm van de stellende trap van (\\w+)"),
            Pattern.compile("meervoud van het zelfstandig naamwoord (\\w+)"),
            Pattern.compile("onverbogen vorm van de vergrotende trap van (\\w+)"),
            Pattern.compile("partitief van de stellende trap van (\\w+)"),
            Pattern.compile("betrekking hebbend op, van de aard van (\\w+)"),

            // Werkwoorden
            Pattern.compile("eerste persoon enkelvoud tegenwoordige tijd van (\\w+)"),
            Pattern.compile("tweede persoon enkelvoud tegenwoordige tijd van (\\w+)"),
            Pattern.compile("derde persoon enkelvoud tegenwoordige tijd van (\\w+)"),
            Pattern.compile("enkelvoud verleden tijd van (\\w+)"),
            Pattern.compile("meervoud verleden tijd van (\\w+)"),
            Pattern.compile("voltooid deelwoord van (\\w+)"),
            Pattern.compile("onvoltooid deelwoord van (\\w+)"),
            Pattern.compile("verouderde gebiedende wijs meervoud van (\\w+)"),
            Pattern.compile("gebiedende wijs van (\\w+)")
    );

    private Optional<String> getRootWord(List<WiktionaryDefinition> definitions) {
        if (definitions.isEmpty()) {
            return Optional.empty();
        }

        String definition = definitions.get(0).getExplanation();

        // Check for adjective
        for (Pattern rootFinder : rootFinders) {
            Matcher rootMatcher = rootFinder.matcher(definition);
            if (rootMatcher.find()) {
                return Optional.of(rootMatcher.group(1));
            }
        }


        return Optional.empty();
    }

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
