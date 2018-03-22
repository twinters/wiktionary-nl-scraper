package be.thomaswinters.wiktionarynl.data;

import java.util.Objects;

public class Language {
    private final String languageName;


    public Language(String languageName) {

        this.languageName = languageName;
    }

    public String getLanguageName() {

        return languageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language that = (Language) o;
        return Objects.equals(languageName, that.languageName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(languageName);
    }
}
