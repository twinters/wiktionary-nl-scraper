package be.thomaswinters.wiktionarynl.data;


import java.util.List;
import java.util.Optional;

public class Definition {
    private final Optional<IWiktionaryWord> rootWord;
    private final Optional<String> category;
    private final String explanation;
    private final List<String> examples;


    public Definition(Optional<String> category, String explanation, List<String> examples, Optional<IWiktionaryWord> rootWord) {
        this.category = category;
        this.explanation = explanation;
        this.examples = examples;
        this.rootWord = rootWord;
    }

    public Optional<String> getCategory() {
        return category;
    }

    public String getExplanation() {
        return explanation;
    }

    public Optional<IWiktionaryWord> getRootWord() {
        return rootWord;
    }

    public Optional<IWiktionaryWord> getTotalRoot() {
        Optional<IWiktionaryWord> totalRoot = getRootWord();
        if (totalRoot.isPresent()) {
            do {
                Optional<IWiktionaryWord> newTotalRoot = totalRoot.get().getDefinitions().getAllDefinitions().stream()
                        .map(definition -> definition.getRootWord())
                        .filter(rootWord -> rootWord.isPresent())
                        .map(rootWord -> rootWord.get())
                        .findFirst();
                if (newTotalRoot.isPresent()) {
                    totalRoot = newTotalRoot;
                } else {
                    break;
                }
            } while (!getRootWord().equals(totalRoot));
        }

        return totalRoot;
    }

    @Override
    public String toString() {
        return (category.isPresent() ? "(" + category.get() + ") " : "") + explanation;
    }


    public List<String> getExamples() {
        return examples;
    }
}
