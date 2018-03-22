package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.Language;
import be.thomaswinters.wiktionarynl.data.WiktionaryWordProxy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootWordRetriever {

    private final BiFunction<String, Language, IWiktionaryWord> scraper;

    public RootWordRetriever(BiFunction<String, Language, IWiktionaryWord> scraper) {
        this.scraper = scraper;
    }

    public Optional<IWiktionaryWord> getRootWord(String word, Language language, String explanation) {
        Optional<IWiktionaryWord> rootWord = Optional.empty();
        Optional<String> possibleRootWord = getRootWord(explanation);
        if (possibleRootWord.isPresent()) {
            String newWord = possibleRootWord.get();
            if (!newWord.equals(word)) {
                Supplier<IWiktionaryWord> loader = () -> scraper.apply(newWord, language);
                rootWord = Optional.of(new WiktionaryWordProxy(loader));
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
