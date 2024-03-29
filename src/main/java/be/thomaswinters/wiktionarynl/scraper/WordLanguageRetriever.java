package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Retrieves the word in a particular language
 */
public class WordLanguageRetriever {
    private final DefinitionsRetriever definitionFinder;
    private final AntonymRetriever antonymFinder;
    private final IWiktionaryWordScraper scraper;

    public WordLanguageRetriever(IWiktionaryWordScraper scraper) {
        this.scraper = scraper;
        this.definitionFinder = new DefinitionsRetriever(new RootWordRetriever(scraper));
        this.antonymFinder = new AntonymRetriever(scraper);
    }

    public WiktionaryWord scrapeWord(String word, Language language, Elements elements) {
        // Get all subsections present in this block
        Map<String, Elements> subsections = collectSubsections(elements);

        // Get all elements
        Map<WordType, List<Definition>> definitions = definitionFinder.retrieveDefinitions(word, language, subsections);
        List<IWiktionaryWord> antonyms = antonymFinder.retrieveAntonyms(language, subsections);

        return new WiktionaryWord(word, new DefinitionList(definitions), antonyms);
    }


    private static final List<String> SUBSECTION_TAGS = Arrays.asList("h4", "h5");

    /**
     * Returns a mapping of titles to the elements below them
     *
     * @param elements
     * @return
     */
    private Map<String, Elements> collectSubsections(Elements elements) {
        Map<String, Elements> subsectionElements = new LinkedHashMap<>();

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
                    putInNewSection(subsectionElements, currentSection, currentRelevantElements);
                }
                currentSection = Optional.of(e.getElementsByAttribute("title").get(0).attr("title"));
                currentRelevantElements = new ArrayList<>();
            } else {
                currentRelevantElements.add(e);
            }
        }

        putInNewSection(subsectionElements, currentSection, currentRelevantElements);

        return subsectionElements;

    }

    private void putInNewSection(Map<String, Elements> subsectionElements, Optional<String> currentSection, List<Element> currentRelevantElements) {
        currentSection.ifPresent(s -> {
            String currentSectionGet = s;
            if (!subsectionElements.containsKey(currentSectionGet)) {
                subsectionElements.put(currentSectionGet, new Elements(currentRelevantElements));
            } else {
                Elements oldElements = subsectionElements.get(currentSectionGet);
                List<Element> newRelevantElements = new ArrayList<>(oldElements);
                newRelevantElements.addAll(currentRelevantElements);
                subsectionElements.put(currentSectionGet, new Elements(newRelevantElements));
            }
        });
    }
}
