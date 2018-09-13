package be.thomaswinters.wiktionarynl.data;


import com.google.common.collect.ImmutableList;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        Set<IWiktionaryWord> handled = new HashSet<>();
        if (totalRoot.isPresent()) {
            do {
                handled.add(totalRoot.get());
                Optional<IWiktionaryWord> newTotalRoot = totalRoot.get().getDefinitions().getAllDefinitions().stream()
                        .map(Definition::getRootWord)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst();
                if (newTotalRoot.isPresent()) {
                    totalRoot = newTotalRoot;
                } else {
                    break;
                }
            } while (!getRootWord().equals(totalRoot) && !handled.contains(totalRoot.get()));
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
