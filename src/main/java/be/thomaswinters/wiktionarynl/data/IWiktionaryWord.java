package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.Map;

public interface IWiktionaryWord {
    String getWord();

    Map<WordType, List<WiktionaryDefinition>> getDefinitions();

    List<IWiktionaryWord> getAntonyms();
}
