package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.Language;
import be.thomaswinters.wiktionarynl.data.WiktionaryDefinition;
import be.thomaswinters.wiktionarynl.data.WordType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static be.thomaswinters.wiktionarynl.data.WordType.*;

public class DefinitionsRetriever {


    private static final Map<String, WordType> WORDTYPE_TITLES;

    static {
        Builder<String, WordType> b = ImmutableMap.builder();
        b.put("WikiWoordenboek:Zelfstandig naamwoord", NOUN);
        b.put("WikiWoordenboek:Werkwoord", VERB);
        b.put("WikiWoordenboek:Bijvoeglijk naamwoord", ADJECTIVE);
        b.put("WikiWoordenboek:Bijwoord", ADVERB);
        b.put("WikiWoordenboek:Voorzetsel", PREPOSITION);
        b.put("WikiWoordenboek:Voegwoord", CONJUNCTION);
        WORDTYPE_TITLES = b.build();
    }

    private final RootWordRetriever rootWordFinder;

    public DefinitionsRetriever(RootWordRetriever rootWordFinder) {
        this.rootWordFinder = rootWordFinder;
    }

    public Map<WordType, List<WiktionaryDefinition>> retrieveDefinitions(String word, Language language, Map<String, Elements> subsections) {

        Builder<WordType, List<WiktionaryDefinition>> builder = ImmutableMap.builder();

        for (Entry<String, Elements> subsection : subsections.entrySet()) {
            // Check if it's a definition
            if (WORDTYPE_TITLES.containsKey(subsection.getKey())) {
                WordType wordType = WORDTYPE_TITLES.get(subsection.getKey());
                Element definitionsList = findNextList(subsection.getValue().first());
                List<WiktionaryDefinition> definitions = definitionsList.children().stream().map(listElement -> getDefinition(word, language, listElement)).collect(Collectors.toList());
                builder.put(wordType, definitions);
            }
        }
        return builder.build();
    }


    private WiktionaryDefinition getDefinition(String word, Language language, Element li) {
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
        String explanation = text.trim();

// TODO: fix rootword in definition
        Optional<IWiktionaryWord> rootWord = rootWordFinder.getRootWord(word, language, explanation);

        return new WiktionaryDefinition(category, explanation, rootWord);
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
}

