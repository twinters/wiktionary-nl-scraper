package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class WordLanguageRetriever {
    private final DefinitionsRetriever definitionFinder;
    private final AntonymRetriever antonymFinder = new AntonymRetriever();

    public WordLanguageRetriever(DefinitionsRetriever definitionFinder) {
        this.definitionFinder = definitionFinder;
    }

    public WiktionaryWord scrapeWord(String word, Language language, Elements elements) {
        Map<String, Elements> subsections = collectSubsections(elements);

        Map<WordType, List<WiktionaryDefinition>> definitions = definitionFinder.retrieveDefinitions(word, language, subsections);

        // TODO: make proxy!
        List<IWiktionaryPage> antonyms = antonymFinder.retrieveAntonyms(null);

        return new WiktionaryWord(word, definitions, antonyms);
    }


    private static final List<String> SUBSECTION_TAGS = Arrays.asList("h4", "h5");

    /**
     * Returns a mapping of titles to the elements below them
     *
     * @param elements
     * @return
     */
    private Map<String, Elements> collectSubsections(Elements elements) {
        Map<String, Elements> subsectionElements = new HashMap<>();

        Optional<String> currentSection = Optional.empty();
        List<Element> currentRelevantElements = new ArrayList<>();
        for (Element e : elements) {
            // Ignore thumbnails
            if (e.className().contains("thumb")) {
                continue;
            }

            // Marker for new language
            if (SUBSECTION_TAGS.contains(e.tag().getName())) {
                if (!currentSection.isPresent() && !currentRelevantElements.isEmpty()) {
//                    System.out.println("WARNING: The following elements got lost due to no present subsection header: " + currentRelevantElements);
                } else {
                    if (currentSection.isPresent()) {
                        subsectionElements.put(currentSection.get(), new Elements(currentRelevantElements));
                    }
                }
                currentSection = Optional.of(e.getElementsByAttribute("title").get(0).attr("title"));
                currentRelevantElements = new ArrayList<>();
            } else {
                currentRelevantElements.add(e);
            }
        }
        if (currentSection.isPresent()) {
            subsectionElements.put(currentSection.get(), new Elements(currentRelevantElements));
        }

        return subsectionElements;

    }
}
