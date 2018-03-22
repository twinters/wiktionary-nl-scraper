package be.thomaswinters.wiktionarynl.scraper;

import be.thomaswinters.wiktionarynl.data.IWiktionaryWord;
import be.thomaswinters.wiktionarynl.data.Language;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface IWiktionaryWordScraper {

    IWiktionaryWord scrape(Language language, String word) throws IOException, ExecutionException;
}
