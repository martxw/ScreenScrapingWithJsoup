# Screen Scraping With jsoup
This project demonstrates how to use [jsoup](http://jsoup.org/) to screen scrape the [Open Farm Sunday](http://www.farmsunday.org) web site to extract the farm locations into a CSV file that can be imported into Google Maps.

To build and run this:
 - Visit http://www.farmsunday.org/ofs12b/visit/findfarm.eb and search
   for all of the farms.
 - Download the search results HTML, overwriting `downloaded.html`.
 - Run `mvn compile exec:java`.
 - You will now have a `locations.csv` file that you can import into "My maps" in Google Maps.

More detailed instructions are in [intro.html](http://martxw.github.io/ScreenScrapingWithJsoup/intro.html).
