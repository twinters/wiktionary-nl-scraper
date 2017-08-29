package be.thomaswinters.wiktionarynl.data;

import java.util.Optional;

public class WiktionaryDefinition {
    private final Optional<String> category;
    private final String explanation;


    public WiktionaryDefinition(Optional<String> category, String explanation) {

        this.category = category;
        this.explanation = explanation;
    }

    public Optional<String> getCategory() {
        return category;
    }

    public String getExplanation() {
        return explanation;
    }
}
