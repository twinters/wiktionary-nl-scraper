package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class WiktionaryPageScraperTest {

    private static final Language NEDERLANDS = new Language("Nederlands");

    private WiktionaryPageScraper retriever;

    @BeforeEach
    public void setup() {
        retriever = new WiktionaryPageScraper();
    }

    @Test
    public void mooiste_root() throws IOException, ExecutionException {

        WiktionaryPage mooisteWords = retriever.scrapePage("mooiste");

        // Check if word has a word
        assertTrue(mooisteWords.getLanguages().contains(NEDERLANDS));
        WiktionaryWord word = mooisteWords.getWord(NEDERLANDS);
        assertEquals(1, word.getDefinitions().getAllDefinitions().size());

        // check for definition correctness
        assertEquals("mooiste", word.getWord());
        assertEquals(1, word.getDefinitions().getAllDefinitions().size());
        List<Definition> adjDefinitions = word.getDefinitions().getDefinition(WordType.ADJECTIVE);
        assertEquals(1, adjDefinitions.size());
        Definition firstDefinition = adjDefinitions.get(0);
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
            assertFalse(page.getLanguages().isEmpty(),input + " doesn't have any languages");
            assertFalse(page.getWord(NEDERLANDS).getDefinitions().getAllDefinitions().isEmpty(), input + " doesn't have Dutch definitions");
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
    public void divergent_antonym() throws IOException, ExecutionException {
        WiktionaryPage page = retriever.scrapePage("divergent");
        assertEquals(Arrays.asList("convergent"), page.getWord(NEDERLANDS).getAntonyms().stream().map(wikiword -> wikiword.getWord()).collect(Collectors.toList()));
    }

    @Test
    public void burgemeester_definition() throws IOException, ExecutionException {
        String word = "burgemeester";
        WiktionaryPage quizPage = retriever.scrapePage(word);
        test_has_definitions(word);
        List<Definition> definitions = quizPage.getWord(NEDERLANDS).getDefinitions().getDefinition(WordType.NOUN);


        assertEquals("hoofd van het gemeentebestuur", definitions.get(0).getExplanation());
        assertEquals("beroep", definitions.get(0).getCategories().get(0));
        assertEquals("het bord onder de spil van een molen", definitions.get(1).getExplanation());
        assertEquals("de naam van een tweetal meeuwensoorten:", definitions.get(2).getExplanation());
        assertEquals(Arrays.asList("de grote burgemeester", "de kleine burgemeester"), definitions.get(2).getExamples());
        assertEquals("vogels", definitions.get(2).getCategories().get(0));
    }

    @Test
    public void quizprogramma_example() throws IOException, ExecutionException {
        String word = "quizprogramma";
        WiktionaryPage quizPage = retriever.scrapePage(word);
        test_has_definitions(word);
        Definition definition = quizPage.getWord(NEDERLANDS).getDefinitions().getDefinition(WordType.NOUN).get(0);


        assertEquals("een televisieprogramma waarin kandidaten onderworpen worden aan vragen waarmee zij prijzen kunnen winnen", definition.getExplanation());
        assertEquals("Twee Voor Twaalf was lange tijd een bekend quizprogramma.", definition.getExamples().get(0));

    }

    @Test
    public void verb_category_test() throws IOException, ExecutionException {
        String word = "verbeteren";
        WiktionaryPage quizPage = retriever.scrapePage(word);
        test_has_definitions(word);
        List<Definition> definitions = quizPage.getWord(NEDERLANDS).getDefinitions().getDefinition(WordType.VERB);


        assertEquals("overgankelijk", definitions.get(0).getCategories().get(0));
        assertEquals("overgankelijk", definitions.get(1).getCategories().get(0));
        assertEquals("ergatief", definitions.get(2).getCategories().get(0));
        assertEquals("wederkerend", definitions.get(3).getCategories().get(0));
    }
}