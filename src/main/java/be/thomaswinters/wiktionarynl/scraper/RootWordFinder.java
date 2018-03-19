package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.WiktionaryDefinition;
import be.thomaswinters.wiktionarynl.data.WiktionaryWordProxy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootWordFinder {
    public Optional<IWiktionaryWord> getRootWord(WiktionaryDataRetriever wiktionaryDataRetriever, String word, List<WiktionaryDefinition> definitions) {
        Optional<IWiktionaryWord> rootWord = Optional.empty();
        if (!definitions.isEmpty()) {
            // Check if in first child, a link is provided
//                        Optional<Element> rootLink = definitionsList.children().get(0).children().stream()
//                                .filter(child -> child.text().contains("(")).flatMap(e -> e.getElementsByTag("a").stream())
//                                .filter(e -> e.attr("href").startsWith("/wiki/")).findFirst();

            Optional<String> possibleRootWord = getRootWord(definitions);

            if (possibleRootWord.isPresent()) {
                String newWord = possibleRootWord.get();
                if (!newWord.equals(word)) {
                    Supplier<IWiktionaryWord> loader = () -> {
                        try {
                            List<IWiktionaryWord> rootWords = wiktionaryDataRetriever.retrieveDefinitions(newWord);
                            if (!rootWords.isEmpty()) {
                                return rootWords.get(0);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                        }
                        throw new RuntimeException("Failed to load rootword");
                    };

                    rootWord = Optional.of(new WiktionaryWordProxy(loader));
                }
            }
        }
        return rootWord;
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
}
