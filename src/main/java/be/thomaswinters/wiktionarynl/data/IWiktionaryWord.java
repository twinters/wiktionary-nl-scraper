package be.thomaswinters.wiktionarynl.data;

import java.util.List;
import java.util.Optional;

public interface IWiktionaryWord {
    String getWord();

    List<WiktionaryDefinition> getDefinitions();

    WordType getWordType();

    Optional<IWiktionaryWord> getRootWord();

    IWiktionaryWord getTotalRootWord();

    List<IWiktionaryWord> getAntonyms();
}
