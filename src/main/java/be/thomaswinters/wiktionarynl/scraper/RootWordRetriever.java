package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootWordRetriever {

    private final WiktionaryPageScraper wiktionaryPageScraper;

    public RootWordRetriever(WiktionaryPageScraper wiktionaryPageScraper) {
        this.wiktionaryPageScraper = wiktionaryPageScraper;
    }

    public Optional<RootWord> getRootWord(String word, Language language, String explanation) {
        Optional<IWiktionaryPage> rootWord = Optional.empty();
//        if (!definitions.isEmpty()) {
        // Check if in first child, a link is provided
//                        Optional<Element> rootLink = definitionsList.children().get(0).children().stream()
//                                .filter(child -> child.text().contains("(")).flatMap(e -> e.getElementsByTag("a").stream())
//                                .filter(e -> e.attr("href").startsWith("/wiki/")).findFirst();

        Optional<String> possibleRootWord = getRootWord(explanation);

        if (possibleRootWord.isPresent()) {
            String newWord = possibleRootWord.get();
            if (!newWord.equals(word)) {
                Supplier<IWiktionaryPage> loader = () -> {
                    System.out.println("Loading page");
                    try {
                        WiktionaryPage rootWordPage = wiktionaryPageScraper.retrieveDefinitions(newWord);
                        return rootWordPage;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                };

                rootWord = Optional.of(new WiktionaryPageProxy(loader));
            }
        }
        return rootWord.map(e -> new RootWord(e, language));
    }

    private List<Pattern> rootFinders = Arrays.asList(
            // Substantief
            Pattern.compile("verkleinwoord enkelvoud van het zelfstandig naamwoord (\\w+)"),

            // Adjectief
            Pattern.compile("verbogen vorm van de stellende trap van (\\w+)"),
            Pattern.compile("verbogen vorm van de overtreffende trap van (\\w+)"),
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

    private Optional<String> getRootWord(String explanation) {

        // Check for adjective
        for (Pattern rootFinder : rootFinders) {
            Matcher rootMatcher = rootFinder.matcher(explanation);
            if (rootMatcher.find()) {
                return Optional.of(rootMatcher.group(1));
            }
        }


        return Optional.empty();
    }
}
