package be.thomaswinters.wiktionarynl.data;

import java.util.ArrayList;
import java.util.List;

public class NonExistingWiktionaryWord implements IWiktionaryWord {
    private final String word;

    public NonExistingWiktionaryWord(String word) {
        this.word = word;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public DefinitionList getDefinitions() {
        return new DefinitionList();
    }

    @Override
    public List<IWiktionaryWord> getAntonyms() {
        return new ArrayList<>();
    }
}
