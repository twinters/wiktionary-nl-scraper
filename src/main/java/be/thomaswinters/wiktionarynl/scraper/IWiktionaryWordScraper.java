package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.Language;

public interface IWiktionaryWordScraper {
    IWiktionaryWord scrape(Language language, String word);
}
