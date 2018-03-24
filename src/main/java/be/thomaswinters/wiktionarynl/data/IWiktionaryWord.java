package be.thomaswinters.wiktionarynl.data;

import java.util.List;

public interface IWiktionaryWord {
    String getWord();

    DefinitionList getDefinitions();

    List<IWiktionaryWord> getAntonyms();
}
