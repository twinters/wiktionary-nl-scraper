package be.thomaswinters.wiktionarynl.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefinitionList {

    private final ImmutableMap<WordType, List<Definition>> definitions;

    public DefinitionList(Map<WordType, List<Definition>> definitions) {
        this.definitions = ImmutableMap.copyOf(definitions);
    }

    public DefinitionList() {
        this(ImmutableMap.<WordType, List<Definition>>builder().build());
    }

    public Optional<Definition> getFirstDefinition() {
        return definitions.values().stream().flatMap(definitions -> definitions.stream()).findFirst();
    }

    public List<Definition> getAllDefinitions() {
        ImmutableList.Builder<Definition> b = ImmutableList.builder();
        definitions.values().stream().forEach(e -> b.addAll(e));
        return b.build();
    }

    public List<Definition> getDefinition(WordType key) {
        return definitions.get(key);
    }

    public Set<WordType> getWordTypes() {
        return definitions.keySet();
    }

    public ImmutableMap getDefinitions() {
        return definitions;
    }

    @Override
    public String toString() {
        return "DefinitionList{" +
                definitions +
                '}';
    }
}
