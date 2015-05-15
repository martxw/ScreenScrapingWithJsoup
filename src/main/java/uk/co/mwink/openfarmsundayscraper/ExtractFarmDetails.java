package uk.co.mwink.openfarmsundayscraper;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Screen scraper for building a Google My Maps compatible CSV file from the Open Farm Sunday listings.
 */
public class ExtractFarmDetails {

    public static void main(String[] args) throws IOException {
        // POST to the Open Farm Sunday web site including parameters takes from the search form.
        // I don't know which are absolutely required, so include nearly all.
        Document doc = Jsoup.connect("http://farmsunday.org/ofs12b/visit/findfarm.eb")
                .data("__EVENTTARGET", "ctl00$contentHolder$el8XF3BGR1XZUNP")
                .data("__EVENTARGUMENT", "search$")
                .data("ctl00$contentHolder$el8XF3BGR1XZUNP$ofs_postcode", "")
                .data("ctl00$contentHolder$el8XF3BGR1XZUNP$ofs_distance", "50")
                .data("ctl00$contentHolder$el8XF3BGR1XZUNP$unit", "mi")
                .data("ctl00$contentHolder$el8XF3BGR1XZUNP$ofs_address", "")
                .data("ctl00$contentHolder$el8XF3BGR1XZUNP$ofs_region", "")
                .data("ctl00$contentHolder$el8XF3BGR1XZUNP$btnSearch", " Sea")
                .post();

        // Save the CSV to a file including the date in its name.
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        String filename = String.format("location_%s.csv", DATE_FORMAT.format(new Date()));
        PrintStream out = new PrintStream(new File(filename));

        // Header for the CSV file.
        out.printf("Name,Postcode,URL\n");

        // The URLs in the HTML are relative to this base URL.
        String baseUrl = "http://www.farmsunday.org/ofs12b/visit/";
        
        // Search for <table class="scrolllist"> and return all of the <tr> elements within it.
        Element[] rowElements = doc.select("table.scrolllist tr").toArray(new Element[0]);

        // The table contains 1 header row, and 2 rows per farm.
        for (int i = 0; i < (rowElements.length - 1) / 2; i++) {
            // Get the alternating "farmevent" and description rows.
            Element farmEvent = rowElements[i * 2 + 1];
            Element farmDescription = rowElements[i * 2 + 2];
            out.printf("%s,%s,%s%s\n",
                    // The farm name is in the 1st <td>.
                    noCommas(farmEvent.select("td:nth-child(1)").text()),
                    // The postcode is in the 2nd <td>.
                    noCommas(farmEvent.select("td:nth-child(2)").text()),
                    // Get the URL from the href attribute in the <a> within the <span class="navlink">
                    baseUrl,
                    noCommas(farmDescription.select(".navlink a").attr("href")));
        }
        out.close();
    }

    // Mustn't have commas in the CSV data.
    static String noCommas(String data) {
        return data.replace(',', ';');
    }
}
