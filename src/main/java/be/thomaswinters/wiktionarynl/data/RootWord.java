package be.thomaswinters.wiktionarynl.data;

import java.util.Optional;

public class RootWord {
    private final IWiktionaryPage wordPage;
    private final Language language;

    public RootWord(IWiktionaryPage wordPage, Language language) {
        this.wordPage = wordPage;
        this.language = language;
    }

    IWiktionaryPage getWordPage() {
        return wordPage;
    }

    Language getLanguage() {
        return language;
    }

    public WiktionaryWord getWord() {
        return wordPage.getWord(language);
    }
    
    public RootWord getTotalRoot() {
        RootWord totalRoot = this;
        do {
            Optional<RootWord> newTotalRoot = totalRoot.getWord().getDefinitions().values().stream().flatMap(e -> e.stream())
                    .map(definition -> definition.getRootWord())
                    .filter(rootWord -> rootWord.isPresent())
                    .map(rootWord -> rootWord.get())
                    .findFirst();
            if (newTotalRoot.isPresent()) {
                totalRoot = newTotalRoot.get();
            } else {
                break;
            }
        } while (!this.equals(totalRoot));
        return totalRoot;
    }
}
