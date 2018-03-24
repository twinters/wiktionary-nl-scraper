package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.Definition;
import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.Language;
import be.thomaswinters.wiktionarynl.data.WordType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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

    public Map<WordType, List<Definition>> retrieveDefinitions(String word, Language language, Map<String, Elements> subsections) {

        Builder<WordType, List<Definition>> builder = ImmutableMap.builder();

        for (Entry<String, Elements> subsection : subsections.entrySet()) {
            // Check if it's a definition
            if (WORDTYPE_TITLES.containsKey(subsection.getKey())) {
                WordType wordType = WORDTYPE_TITLES.get(subsection.getKey());
                Optional<Element> definitionsList = findNextList(subsection.getValue().first());
                if (definitionsList.isPresent()) {
                    List<Definition> definitions = definitionsList.get().children().stream().map(listElement -> getDefinition(word, language, listElement)).collect(Collectors.toList());
                    builder.put(wordType, definitions);
                }
            }
        }
        return builder.build();
    }


    private Definition getDefinition(String word, Language language, Element li) {

        // Get examples
        Elements exampleDl = li.select("dd");

        List<String> examples = exampleDl.stream()
                // Trim using special trim character replacement
                .map(e -> e.text().replaceAll("\u00A0", " ").trim())
                .collect(Collectors.toList());


        // Remove examples from definition: Remove dl and dd
        Elements examplesDl = li.select("dl");
        examplesDl.remove();

        List<String> categories = new ArrayList<>();
        // Check if there is something with a title
        Elements titleLinksElements = li.select("a[title=WikiWoordenboek:Werkwoord]");
        for (Element el : titleLinksElements) {
            categories.add(el.text());
            el.remove();
        }

        String text = li.text();

        // Check if a category is specified between brackets
        int firstClosingBracket = text.indexOf(")");
        int firstOpeningBracket = text.indexOf("(");
        while (firstOpeningBracket == 0 && firstClosingBracket > firstOpeningBracket) {
            String categoryText = text.substring(firstOpeningBracket + 1, firstClosingBracket);
            categories.add(categoryText);
            text = text.substring(firstClosingBracket + 1).trim();

            if (text.startsWith(", ")) {
                text = text.substring(2);
            }

            firstClosingBracket = text.indexOf(")");
            firstOpeningBracket = text.indexOf("(");
        }

        String explanation = text.trim();


        Optional<IWiktionaryWord> rootWord = rootWordFinder.getRootWord(word, language, explanation);

        return new Definition(categories, explanation, examples, rootWord);
    }

    private Optional<Element> findNextList(Element e) {
        Element current = e;
        while (current != null && !current.tag().getName().equals("ol") && !current.tag().getName().equals("ul")) {
            current = current.nextElementSibling();
        }
        return Optional.ofNullable(current);
    }


    private boolean isHeader(Element e) {
        return e.tag().getName().equals("h3") || e.tag().getName().equals("h4") || e.tag().getName().equals("h5");
    }
}

