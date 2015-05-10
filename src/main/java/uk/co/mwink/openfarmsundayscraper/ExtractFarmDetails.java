package uk.co.mwink.openfarmsundayscraper;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Screen scraper for building a Google Maps compatible CSV file from the Open Farm Sunday listings.
 */
public class ExtractFarmDetails {

    public static void main(String[] args) throws IOException {
        // The HTML, saved to a local file, resulting from a POSTed form.
        File input = new File(args[0]);

        PrintStream out = new PrintStream(new File(args[1]));
        // Header for the CSV file.
        out.printf("Name,Postcode,URL\n");

        // Open the HTML, and reference the original web site.
        Document doc = Jsoup.parse(input, "UTF-8", "http://www.farmsunday.org/");

        // Search for <table class="scrolllist"> and return all of the <tr> elements within it.
        Element[] rowElements = doc.select("table.scrolllist tr").toArray(new Element[0]);

        // The table contains 1 header row, and 2 rows per farm.
        for (int i = 0; i < (rowElements.length - 1) / 2; i++) {
            // Get the alternating "farmevent" and description rows.
            Element farmEvent = rowElements[i * 2 + 1];
            Element farmDescription = rowElements[i * 2 + 2];
            out.printf("%s,%s,%s\n",
                    // The farm name is in the 1st <td>.
                    noCommas(farmEvent.select("td:nth-child(1)").text()),
                    // The postcode is in the 2nd <td>.
                    noCommas(farmEvent.select("td:nth-child(2)").text()),
                    // Get the URL from the href attribute in the <a> within the <span class="navlink">
                    noCommas(farmDescription.select(".navlink a").attr("href")));
        }
        out.close();
    }

    // Mustn't have commas in the CSV data.
    static String noCommas(String data) {
        return data.replace(',', ';');
    }
}
