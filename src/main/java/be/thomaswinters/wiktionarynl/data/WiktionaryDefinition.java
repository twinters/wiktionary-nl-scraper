package be.thomaswinters.wiktionarynl.data;

import java.util.Optional;

public class WiktionaryDefinition {
    private final Optional<RootWord> rootWord;
    private final Optional<String> category;
    private final String explanation;


    public WiktionaryDefinition(Optional<String> category, String explanation, Optional<RootWord> rootWord) {
        this.category = category;
        this.explanation = explanation;
        this.rootWord = rootWord;
    }

    public Optional<String> getCategory() {
        return category;
    }

    public String getExplanation() {
        return explanation;
    }

    public Optional<RootWord> getRootWord() {
        return rootWord;
    }
    
    @Override
    public String toString() {
        return (category.isPresent() ? "(" + category.get() + ") " : "") + explanation;
    }


}
