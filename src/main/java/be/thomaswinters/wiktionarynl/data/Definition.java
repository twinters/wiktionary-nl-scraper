package be.thomaswinters.wiktionarynl.data;


import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Definition {
    private final Optional<IWiktionaryWord> rootWord;
    private final List<String> categories;
    private final String explanation;
    private final List<String> examples;


    public Definition(List<String> category, String explanation, List<String> examples, Optional<IWiktionaryWord> rootWord) {
        this.categories = ImmutableList.copyOf(category);
        this.explanation = explanation;
        this.examples = examples;
        this.rootWord = rootWord;
    }

    public List<String> getCategories() {
        return categories;
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
        return (categories.isEmpty() ? "" : "(" + categories.stream().collect(Collectors.joining(", ")) + ") ") + explanation;
    }


    public List<String> getExamples() {
        return examples;
    }
}
