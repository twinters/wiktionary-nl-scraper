package be.thomaswinters.wiktionarynl.util;

import be.thomaswinters.wiktionarynl.data.*;
import be.thomaswinters.wiktionarynl.scraper.WiktionaryPageScraper;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Class that helps finding the definition of a word by following the linked paths in definitions until hitting
 * a rootword that actually has a real definition rather than a reference.
 */
public class RootWordDefinitionFinder {
    private final WiktionaryPageScraper definitionFinder = new WiktionaryPageScraper();

    private final Language languageToUse;

    public RootWordDefinitionFinder(Language usedLanguage) {
        this.languageToUse = usedLanguage;
    }

    public Optional<String> getRootWordDefinition(String inputWord) throws IOException, ExecutionException {
        WiktionaryPage wiktionaryPage = definitionFinder.scrapePage(inputWord.toLowerCase());
        if (!wiktionaryPage.getLanguages().isEmpty()) {

            // Add prefix if it isn't Dutch
            String prefix = "";
            Language usedLanguage = languageToUse;
            if (!wiktionaryPage.hasLanguage(usedLanguage)) {
                usedLanguage = wiktionaryPage.getLanguages().iterator().next();
                prefix = usedLanguage.getLanguageName() + " voor ";
            }

            // Find definition of root
            DefinitionList definitions = wiktionaryPage.getWord(usedLanguage).getDefinitions();

            for (Definition definition : definitions.getAllDefinitions()) {
                if (!definition.getRootWord().isPresent()) {
                    System.out.println("Returning '" + definition.getExplanation() + "' for " + inputWord);
                    return Optional.of(prefix + definition.getExplanation());
                }
            }

            Optional<Definition> definition = definitions.getFirstDefinition();
            if (definition.isPresent()) {

                // Check if it has a rootword
                Optional<IWiktionaryWord> rootWord = definition.get().getTotalRoot();
                if (rootWord.isPresent()) {
                    Optional<Definition> newDefinition = rootWord.get().getDefinitions().getFirstDefinition();
                    if (newDefinition.isPresent()) {
                        definition = newDefinition;
                    }
                }

                String explanation = definition.get().getExplanation();
                return Optional.of(prefix + explanation);

            }

        }
        return Optional.empty();
    }
}
