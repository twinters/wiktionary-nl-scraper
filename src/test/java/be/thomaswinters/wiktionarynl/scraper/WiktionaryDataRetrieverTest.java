package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class WiktionaryDataRetrieverTest {

    private WiktionaryDataRetriever retriever;

    @Before
    public void setup() {
        retriever = new WiktionaryDataRetriever();
    }

    @Test
    public void mooiste_root() throws IOException, ExecutionException {

        List<IWiktionaryWord> mooisteWords = retriever.retrieveDefinitions("mooiste");

        // Check if word has a word
        assertEquals(1, mooisteWords.size());
        IWiktionaryWord word = mooisteWords.get(0);

        // check for definition correctness
        assertEquals("mooiste", word.getWord());
        assertEquals(1, word.getDefinitions().size());
        assertEquals("verbogen vorm van de overtreffende trap van mooi", word.getDefinitions().get(0).getExplanation());

        // Root word
        assertTrue(word.getRootWord().isPresent());
        assertEquals("mooi", word.getRootWord().get().getWord());

    }

    @Test
    public void test_definitions_existance_list() throws IOException, ExecutionException {

        List<String> words = Arrays.asList("burgemeester", "mooi", "voor", "tegen", "massamoordenaar", "noemen", "noem");
        for (String word : words) {
            test_has_definitions(word);
        }

    }

    public void test_has_definitions(String input) throws IOException, ExecutionException {
        List<IWiktionaryWord> mooisteWords = retriever.retrieveDefinitions(input);

        // Check if word has a word
        assertFalse(input + " doesn't have a definition", mooisteWords.isEmpty());
    }

    @Test
    public void lelijk_antonym() throws IOException, ExecutionException {
        List<IWiktionaryWord> lelijkWords = retriever.retrieveDefinitions("lelijk");
    }
}