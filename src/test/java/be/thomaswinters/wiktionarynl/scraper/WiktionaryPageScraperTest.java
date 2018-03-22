package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class WiktionaryPageScraperTest {

    private static final Language NEDERLANDS = new Language("Nederlands");

    private WiktionaryPageScraper retriever;

    @Before
    public void setup() {
        retriever = new WiktionaryPageScraper();
    }

    @Test
    public void mooiste_root() throws IOException, ExecutionException {

        WiktionaryPage mooisteWords = retriever.retrieveDefinitions("mooiste");

        // Check if word has a word
        assertTrue(mooisteWords.getLanguages().contains(NEDERLANDS));
        WiktionaryWord word = mooisteWords.getWord(NEDERLANDS);
        assertEquals(1, word.getDefinitions().size());

        // check for definition correctness
        assertEquals("mooiste", word.getWord());
        assertEquals(1, word.getDefinitions().size());
        List<WiktionaryDefinition> adjDefinitions = word.getDefinitions().get(WordType.ADJECTIVE);
        assertEquals(1, adjDefinitions.size());
        WiktionaryDefinition firstDefinition = adjDefinitions.get(0);
        assertEquals("verbogen vorm van de overtreffende trap van mooi", firstDefinition.getExplanation());

        // Root word
        assertTrue(firstDefinition.getRootWord().isPresent());
        assertEquals("mooi", firstDefinition.getRootWord().get().getWord().getWord());
        assertEquals("mooi", firstDefinition.getRootWord().get().getTotalRoot().getWord().getWord());

    }

    @Test
    public void test_definitions_existance_list() throws IOException, ExecutionException {

        List<String> words = Arrays.asList("burgemeester", "mooi", "voor", "tegen", "massamoordenaar", "noemen", "noem");
        for (String word : words) {
            test_has_definitions(word);
        }

    }

    public void test_has_definitions(String input) throws IOException, ExecutionException {
        try {
            WiktionaryPage page = retriever.retrieveDefinitions(input);

            // Check if word has a word
            assertFalse(input + " doesn't have any languages", page.getLanguages().isEmpty());
            assertFalse(input + " doesn't have Dutch definitions", page.getWord(NEDERLANDS).getDefinitions().isEmpty());
        } catch (Exception e) {
            fail(input + " gave the following exception: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void lelijk_antonym() throws IOException, ExecutionException {
        WiktionaryPage lelijkWords = retriever.retrieveDefinitions("lelijk");
    }
}