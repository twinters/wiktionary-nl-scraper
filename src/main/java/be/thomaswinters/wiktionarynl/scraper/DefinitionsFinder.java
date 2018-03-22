package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.WiktionaryDefinition;
import be.thomaswinters.wiktionarynl.data.WordType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static be.thomaswinters.wiktionarynl.data.WordType.*;

public class DefinitionsFinder {


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

    private final RootWordFinder rootWordFinder = new RootWordFinder();


    public Map<WordType, List<WiktionaryDefinition>> retrieveDefinitions(Document doc) {

        Builder<WordType, List<WiktionaryDefinition>> builder = ImmutableMap.builder();

        // Find a defition, and make the list only use uniques
        List<Element> wordtypeTitles = doc.getElementsByAttribute("title").stream().filter(e -> WORDTYPE_TITLES.keySet().contains(e.attr("title"))).distinct().collect(Collectors.toList());

//        List<Element> nounLinkStart = doc.getElementsByAttributeValue("title", WORDTYPE_TITLES.get(NOUN)).stream().distinct().collect(Collectors.toList());

        for (Element element : wordtypeTitles) {// Iterate until we find the nounHeaderElement
            Optional<Element> nounHeaderElement = element.parents().stream().filter(this::isHeader).findFirst();
            if (nounHeaderElement.isPresent()) {
                Element definitionsList = findNextList(nounHeaderElement.get());
                System.out.println(element);
                WordType wordType = WORDTYPE_TITLES.get(element.attr("title"));
                List<WiktionaryDefinition> definitions = definitionsList.children().stream().map(this::getDefinition).collect(Collectors.toList());
                builder.put(wordType, definitions);
            }
        }


        return builder.build();
    }


    private WiktionaryDefinition getDefinition(Element li) {
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

// TODO: fix rootword in definition
//        Optional<IWiktionaryWord> rootWord = rootWordFinder.getRootWord(this, word, definitions);

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
}

