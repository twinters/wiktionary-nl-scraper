package be.thomaswinters.wiktionarynl;

import be.thomaswinters.wiktionarynl.data.WiktionaryDefinition;
import be.thomaswinters.wiktionarynl.data.WiktionaryWord;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WiktionaryDataRetriever {

    private static final String BASE_URL = "https://nl.wiktionary.org/wiki/";

    private static final String NOUN_LINK_TITLE = "WikiWoordenboek:Zelfstandig naamwoord";


    public List<WiktionaryWord> retrieveDefinitions(String word) throws IOException {
        List<WiktionaryWord> result = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(BASE_URL + word).get();
        } catch (HttpStatusException e) {
            return result;
        }

        // Find a defition, and make the list only use uniques
        List<Element> nounLinkStart = doc.getElementsByAttributeValue("title", NOUN_LINK_TITLE).stream().distinct().collect(Collectors.toList());

        for (Element element : nounLinkStart) {// Iterate until we find the nounHeaderElement
            Optional<Element> nounHeaderElement = element.parents().stream().filter(this::isHeader).findFirst();
            if (nounHeaderElement.isPresent()) {
                Element definitionsList = findNextList(nounHeaderElement.get());

                List<WiktionaryDefinition> definitions = definitionsList.children().stream().map(this::getDefition).collect(Collectors.toList());

                result.add(new WiktionaryWord(word, definitions));
            }
        }


        return result;
    }

    private WiktionaryDefinition getDefition(Element li) {
        String text = li.text();

        // Remove dl and dd
        List<Element> toRemoveElements = li.children().stream().filter(e -> e.tagName().equals("dl")).collect(Collectors.toList());
        for (Element e : toRemoveElements) {
            text = text.replaceAll(e.text(), "");
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

    public static void main(String[] args) throws IOException {

        System.out.println(new WiktionaryDataRetriever().retrieveDefinitions("burgemeester"));
    }
}
