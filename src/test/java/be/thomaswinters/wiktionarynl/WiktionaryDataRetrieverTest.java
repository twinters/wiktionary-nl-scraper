package be.thomaswinters.wiktionarynl;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class WiktionaryDataRetrieverTest {

    private WiktionaryDataRetriever retriever;

    @Before
    private void setup() {
        retriever = new WiktionaryDataRetriever();
    }

    @Test
    private void test_root() throws IOException, ExecutionException {

        List<IWiktionaryWord> mooisteWords = retriever.retrieveDefinitions("mooiste");
        assertEquals(1, mooisteWords.size());
        assertEquals("mooi", mooisteWords.get(0).getRootWord().get().getWord());

    }

}