package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

        WiktionaryPage mooisteWords = retriever.scrapePage("mooiste");

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
        assertEquals("mooi", firstDefinition.getRootWord().get().getWord());
        assertEquals("mooi", firstDefinition.getTotalRoot().get().getWord());

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
            WiktionaryPage page = retriever.scrapePage(input);

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
        WiktionaryPage lelijkWords = retriever.scrapePage("lelijk");
        assertEquals(Arrays.asList("mooi"), lelijkWords.getWord(NEDERLANDS).getAntonyms().stream().map(wikiword -> wikiword.getWord()).collect(Collectors.toList()));
    }

    @Test
    public void burgemeester_definition() throws IOException, ExecutionException {
        String word = "burgemeester";
        WiktionaryPage quizPage = retriever.scrapePage(word);
        test_has_definitions(word);
        List<WiktionaryDefinition> definitions = quizPage.getWord(NEDERLANDS).getDefinitions().get(WordType.NOUN);


        assertEquals("hoofd van het gemeentebestuur", definitions.get(0).getExplanation());
        assertEquals("beroep", definitions.get(0).getCategory().get());
        assertEquals("het bord onder de spil van een molen", definitions.get(1).getExplanation());
        assertEquals("de naam van een tweetal meeuwensoorten:", definitions.get(2).getExplanation());
        assertEquals(Arrays.asList("de grote burgemeester", "de kleine burgemeester"), definitions.get(2).getExamples());
        assertEquals("vogels", definitions.get(2).getCategory().get());
    }

    @Test
    public void quizprogramma_example() throws IOException, ExecutionException {
        String word = "quizprogramma";
        WiktionaryPage quizPage = retriever.scrapePage(word);
        test_has_definitions(word);
        WiktionaryDefinition definition = quizPage.getWord(NEDERLANDS).getDefinitions().get(WordType.NOUN).get(0);


        assertEquals("een televisieprogramma waarin kandidaten onderworpen worden aan vragen waarmee zij prijzen kunnen winnen", definition.getExplanation());
        assertEquals("Twee Voor Twaalf was lange tijd een bekend quizprogramma.", definition.getExamples().get(0));

    }
}