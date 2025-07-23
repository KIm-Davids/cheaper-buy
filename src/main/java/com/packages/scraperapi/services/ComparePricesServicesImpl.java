package com.packages.scraperapi.services;

import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
//import com.packages.scraperapi.utilities.BestMatchingProductResultImpl;
import com.packages.scraperapi.repository.ProductRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComparePricesServicesImpl implements ComparePricesServicesInterface {

    private final List<ProductResult> allResults = new ArrayList<>();

    private final ProductRepository repository;
    public ComparePricesServicesImpl(ProductRepository repository){
        this.repository = repository;
    }

    @Override
    public List<ProductResult> scrapeJiji(Query query) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // or just "--headless" if needed
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        WebDriverManager.chromedriver().setup();
//        WebDriver driver = new ChromeDriver();

        List<ProductResult> productResultList = new ArrayList<>();
        int page = 1;
        int maxPages = 5;

        while (page <= maxPages) {
            String searchUrl = "https://jiji.ng/search?query=" + query.getQuery().replace(" ", "+") + "&page=" + page;
            driver.get(searchUrl);

            try {
                Thread.sleep(1000); // Let the page load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Document doc = Jsoup.parse(driver.getPageSource());
            Elements productCards = doc.select("div.b-list-advert__gallery__item.js-advert-list-item");

            if (productCards.isEmpty()) break;

            for (Element card : productCards) {
                // Link
                Element linkTag = card.selectFirst("a[href]");
                if (linkTag == null) continue;
                String fullLink = "https://jiji.ng" + linkTag.attr("href");

                // Title
                String title = card.selectFirst(".b-advert-title-inner.qa-advert-title") != null
                        ? card.selectFirst(".b-advert-title-inner.qa-advert-title").text()
                        : "No title";

                // Price
                String price = card.selectFirst(".qa-advert-price") != null
                        ? card.selectFirst(".qa-advert-price").text()
                        : "No price";

                // Description
                String description = card.selectFirst(".b-list-advert-base__description-text") != null
                        ? card.selectFirst(".b-list-advert-base__description-text").text()
                        : "No description";

                // Image
                Element imageTag = card.selectFirst("picture img");
                String imageUrl = imageTag != null ? imageTag.attr("src") : "";

                double numericPrice = extractNumericPrice(price);

                if (numericPrice >= query.getBudgetAmount()) {
                    ProductResult result = new ProductResult(
                            "jiji", title, price, fullLink, imageUrl, description
                    );
                    productResultList.add(result);
                    if (productResultList.size() >= 20) break;

                }
            }

            productResultList.sort((a, b) -> {
                double diffA = query.getBudgetAmount() - extractNumericPrice(a.getPrice());
                double diffB = query.getBudgetAmount() - extractNumericPrice(b.getPrice());
                return Double.compare(diffA, diffB);
            });


            page++;
        }

        driver.quit();

        // Sort by price ascending
        productResultList.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));

        return productResultList;
    }

//    @Override
//    public ProductResult scrapeJumia(Query query) {
////        try {
////            String searchUrl = "https://www.jumia.com.ng/catalog/?q=" + URLEncoder.encode(query.getQuery(), "UTF-8");
////            Document doc = Jsoup.connect(searchUrl)
////                    .userAgent("Mozilla")
////                    .timeout(10_000)
////                    .get();
////
////            Elements products = doc.select("article.prd");
////            String cheapestTitle = "Not Found";
////            String cheapestPrice = "N/A";
////            String cheapestLink = "#";
////            double lowestPrice = query.getBudgetAmount();
////
////            for(Element product : products){
////                try {
////                    String priceText = product.selectFirst(".prc").text();
////                    String title = product.selectFirst("h3").text();
//////                    String link = product.selectFirst("a").attr("href");
////                    Element anchor = product.selectFirst("a.core");
////                    String href = (anchor != null) ? anchor.attr("href") : "#";
////                    String link = href.startsWith("http") ? href : "https://www.jumia.com.ng" + href;
////
////                    String clean = priceText.replaceAll("[^\\d]", "");
////                    double price = Double.parseDouble(clean);
////                    System.out.println(price);
////                    System.out.println(cheapestPrice);
////
////                    if (price <= lowestPrice) {
////                        lowestPrice = price;
////                        cheapestTitle = title;
////                        cheapestPrice = priceText;
////                        cheapestLink = link;
////                    }
////                }catch (Exception e){
////                    continue;
////                }
////            }
////            return new ProductResult("Jumia", cheapestTitle, cheapestPrice, cheapestLink);
////
////        }catch(Exception e){
////            return new ProductResult("Jumia", "Not Found", "N/A", "#");
////        }
//        return null;
//    }
    @Override
    public List<ProductResult> scrapeJumia(Query query) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // or just "--headless" if needed
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        WebDriverManager.chromedriver().setup();
//        WebDriver driver = new ChromeDriver();

        WebDriverManager.chromedriver().setup();
        List<ProductResult> productList = new ArrayList<>();
        int page = 1;
        int maxPages = 5;

        while (page <= maxPages) {
            String searchUrl = "https://www.jumia.com.ng/catalog/?q=" + query.getQuery().replace(" ", "+") + "&page=" + page;
            driver.get(searchUrl);

            try {
                Thread.sleep(1000); // Wait for page to load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Document doc = Jsoup.parse(driver.getPageSource());
            Elements products = doc.select("article.prd");

            if (products.isEmpty()) break;

            for (Element product : products) {
                try {
                    String title = product.selectFirst("h3.name").text();
                    String price = product.selectFirst("div.prc").text();
                    Element linkElement = product.selectFirst("a.core");
                    String link = linkElement != null ? "https://www.jumia.com.ng" + linkElement.attr("href") : "#";
                    Element imgElement = product.selectFirst("img");
                    String imageUrl = imgElement != null ? imgElement.attr("data-src") : "";


                    double numericPrice = extractNumericPrice(price);


                    if (numericPrice <= query.getBudgetAmount()) {
                        productList.add(new ProductResult("jumia", title, price, link, imageUrl, ""));
                        if (productList.size() >= 20) break;

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            page++;
        }

        driver.quit();

        // Sort results by price
//        productList.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));
        // Final sorting based on closeness to the budget (descending from budget)
        productList.sort((a, b) -> {
            double diffA = query.getBudgetAmount() - extractNumericPrice(a.getPrice());
            double diffB = query.getBudgetAmount() - extractNumericPrice(b.getPrice());
            return Double.compare(diffA, diffB);
        });



        return productList;
    }

    @Override
    public List<ProductResult> scrapeKonga(Query query) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // or just "--headless" if needed
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);

        List<ProductResult> results = new ArrayList<>();
        int page = 1;
        int maxPages = 5;

        while (page <= maxPages) {
            String searchUrl = "https://www.konga.com/search?search=" + URLEncoder.encode(query.getQuery(), StandardCharsets.UTF_8) + "&page=" + page;
            driver.get(searchUrl);

            try {
                Thread.sleep(5000); // Wait for page to load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Document doc = Jsoup.parse(driver.getPageSource());
            Elements products = doc.select("li.List_listItem__KlvU2");

            if (products.isEmpty()) break;

            for (Element product : products) {
                try {
                    // Extract title
                    String title = product.select("h3.ListingCard_productTitle__9Kzxv").text();

                    // Extract price
                    String price = product.select("span.shared_initialPrice__cTRSe").text();

                    // Extract link
                    String relativeLink = product.select("a").attr("href");
                    String fullLink = "https://www.konga.com" + relativeLink;

                    // Extract image
                    Element img = product.selectFirst("img");
                    String imageUrl = img != null ? img.attr("src") : "Image not found";


                    double numericPrice = extractNumericPrice(price);
                    if (numericPrice <= query.getBudgetAmount()) {
                        results.add(new ProductResult("konga", title, fullLink, imageUrl, price, ""));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            page++;
        }

        driver.quit();

        // Sort results by price
        results.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));


        return results;
    }

//    Faster but not accurate
//    public List<ProductResult> scrapeKonga(Query query) {
//
//        List<ProductResult> results = new ArrayList<>();
//        int page = 1;
//        int maxPages = 5;
//
//        try (Playwright playwright = Playwright.create()) {
//            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
//            BrowserContext context = browser.newContext();
//            Page pageObj = context.newPage();
//
//            while (page <= maxPages) {
//                String searchUrl = "https://www.konga.com/search?search=" + URLEncoder.encode(query.getQuery(), StandardCharsets.UTF_8) + "&page=" + page;
//                pageObj.navigate(searchUrl);
//
//                // Wait for page to load
//                pageObj.waitForSelector("li.List_listItem__KlvU2");
//
//                // Parse with Jsoup
//                String content = pageObj.content();
//                Document doc = Jsoup.parse(content);
//                Elements products = doc.select("li.List_listItem__KlvU2");
//
//                if (products.isEmpty()) break;
//
//                for (Element product : products) {
//                    try {
//                        String title = product.select("h3.ListingCard_productTitle__9Kzxv").text();
//                        String price = product.select("span.shared_initialPrice__cTRSe").text();
//                        String relativeLink = product.select("a").attr("href");
//                        String fullLink = "https://www.konga.com" + relativeLink;
//
//                        Element img = product.selectFirst("img");
//                        String imageUrl = img != null ? img.attr("src") : "Image not found";
//
//                        System.out.println("Title: " + title);
//                        System.out.println("Price: " + price);
//                        System.out.println("Link: " + fullLink);
//                        System.out.println("Image URL: " + imageUrl);
//                        System.out.println("----------------------------------------");
//
//                        double numericPrice = extractNumericPrice(price);
//                        if (numericPrice <= query.getBudgetAmount()) {
//                            results.add(new ProductResult("konga", title, fullLink, imageUrl, price, ""));
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//                page++;
//            }
//
//            browser.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        results.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));
//        return results;
//    }

    @Override
    public List<ProductResult> scrapeKusnap(Query query) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // or just "--headless" if needed
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);

        List<ProductResult> productList = new ArrayList<>();
        int page = 1;
        int maxPages = 5;

        while (page <= maxPages) {
            String searchUrl = "https://www.kusnap.com/search?q=" + query.getQuery().replace(" ", "+") + "&page=" + page;
            driver.get(searchUrl);

            try {
                Thread.sleep(5000); // wait for JS-rendered content
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Document doc = Jsoup.parse(driver.getPageSource());

            // Select the anchor tag that wraps each product
            Elements products = doc.select("a[href^=\"/product/\"]");

            if (products.isEmpty()) break;

            for (Element product : products) {
                try {
                    // Title
                    String title = product.selectFirst("p[title]").text();

                    // Price
                    Element priceBlock = product.selectFirst("div.flex.flex-col.pb-2");
                    String price = "";
                    if (priceBlock != null) {
                        Elements spans = priceBlock.select("span");
                        if (!spans.isEmpty()) {
                            price = spans.first().text();
                        }
                    }

                    // Link
                    String relativeLink = product.attr("href");
                    String link = "https://www.kusnap.com" + relativeLink;

                    // Image
                    Element imgElement = product.selectFirst("img");
                    String imageUrl = imgElement != null ? imgElement.attr("src") : "";

                    // Debug

                    double numericPrice = extractNumericPrice(price);
                    if (numericPrice <= query.getBudgetAmount()) {
                        productList.add(new ProductResult("kusnap", title, price, link, imageUrl, ""));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            page++;
        }

        driver.quit();

        // Sort by price
        productList.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));

        return productList;
    }


//    @Override
//    public List<ProductResult> scrapeJiji(Query query) {
//        WebDriverManager.chromedriver().setup();
//        WebDriver driver = new ChromeDriver();
//
//        String searchUrl = "https://jiji.ng/search?query=" + query.getQuery().replace(" ", "+");
//        driver.get(searchUrl);
//
//        try {
//            Thread.sleep(5000); // Let the page load
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        String pageSource = driver.getPageSource();
//        driver.quit();
//
//        Document doc = Jsoup.parse(pageSource);
//        Elements products = doc.select("div.b-list-advert-base__data__inner");
//        List<ProductResult> productResultList = new ArrayList<>();
//
//        for (Element product : products) {
//            String title = product.select(".b-advert-title-inner.qa-advert-title").text();
//            String price = product.select(".qa-advert-price").text();
//            String description = product.select(".b-list-advert-base__description-text").text();
//
//            double numericPrice = extractNumericPrice(price);
//            if (numericPrice >= query.getBudgetAmount()) {
//                productResultList.add(new ProductResult("jiji", title, price, description));
//
//                System.out.println("Title: " + title);
//                System.out.println("Price: " + price);
//                System.out.println("Description: " + description);
//                System.out.println("------");
//            }
//        }
//
//        // Sort filtered results by price ascending
//        productResultList.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));
//
//        return productResultList;
//    }



    private List<ProductResult> filterByQueryAndBudget(List<ProductResult> productList, Query query) {
        String searchQuery = query.getQuery().toLowerCase().trim();
        double budget = query.getBudgetAmount();

        return productList.stream()
                .filter(p -> {
                    String title = p.getTitle().toLowerCase();

                    // Title must match search query (loosely)
                    boolean matchesQuery = title.contains(searchQuery) || Arrays.stream(searchQuery.split(" "))
                            .allMatch(word -> title.contains(word));

                    // Price must match exactly
                    double numericPrice = extractNumericPrice(p.getPrice());
                    boolean exactPriceMatch = Double.compare(numericPrice, budget) == 0;

                    return matchesQuery && exactPriceMatch;
                })
                .collect(Collectors.toList()); // No need to sort since all are exact match
    }



public List<ProductResult> getFilteredProducts(Query query) {
    List<ProductResult> allResults = new ArrayList<>();

    allResults.addAll(scrapeJumia(query));
    allResults.addAll(scrapeKonga(query));
    allResults.addAll(scrapeKusnap(query));
    allResults.addAll(scrapeJiji(query));
    repository.saveAll(allResults);
    return filterByQueryAndBudget(allResults, query);
}

    private static int extractNumericPrice(String priceText) {
        try {
            String numeric = priceText.replaceAll("[^\\d]", "");
            return Integer.parseInt(numeric);
        } catch (Exception e) {
            return Integer.MAX_VALUE; // In case of parse failure
        }
    }


//    public List<ProductResult> handleHybridSearch(Query query) {
//        // 1. Search DB cache
//        List<ProductResult> cached = repository.findByTitleIgnoreCaseContainingAndNumericPriceLessThanEqualOrderByNumericPriceDesc(query.getQuery(), query.getBudgetAmount());
//
//        if (!cached.isEmpty()) {
//            return cached;
//        }else{
//
//        }
//
//        // 2. If not in DB, scrape from Jumia
//        List<ProductResult> jumiaResults = scrapeJumia(query);
//
//        if (!jumiaResults.isEmpty()) {
//            // Save to DB for caching
////            jumiaResults.forEach(p -> p.setQuery(query.getQuery()));
//            repository.saveAll(jumiaResults);
//            return jumiaResults;
//        }
//
//        // 3. Fallback: scrape other sources
//        List<ProductResult> otherResults = new ArrayList<>();
//        otherResults.addAll(scrapeKonga(query));
//        otherResults.addAll(scrapeJiji(query));
//        otherResults.addAll(scrapeKusnap(query));
//
//        if (!otherResults.isEmpty()) {
////            otherResults.forEach(p -> p.setQuery(query.getQuery()));
//            repository.saveAll(otherResults);
//        }
//
//        return otherResults;
//    }

    private Double parsePrice(String priceStr) {
        try {
            // Remove "₦", commas, and whitespace
            String clean = priceStr.replaceAll("[^\\d.]", "");
            return Double.parseDouble(clean);
        } catch (Exception e) {
            return Double.MAX_VALUE; // Put invalid price entries at the end
        }
    }


    public List<ProductResult> handleSearchQueries(Query query) {
        Double targetPrice = query.getBudgetAmount();
        List<ProductResult> productResults = new ArrayList<>();

        // 1. Try DB cache
        List<ProductResult> cached = repository.findByTitleContainingIgnoreCase(
                query.getQuery()
        );

        if (!cached.isEmpty()) {
            return cached;
        }else{
            List<ProductResult> jijiResults = scrapeJiji(query);
            repository.saveAll(jijiResults);
            for (ProductResult product : jijiResults) {
                if (product.getPrice().equals(targetPrice.toString())) {
                    productResults.add(product);
                    return List.of(product);
                }
            }

            // 3. Try Jumia
            List<ProductResult> jumiaResults = scrapeJumia(query);
            repository.saveAll(jumiaResults);
            for (ProductResult product : jumiaResults) {
                if (product.getPrice().equals(targetPrice.toString())) {
                    productResults.add(product);
                    return List.of(product);
                }
            }

            // 4. Try Konga
//            List<ProductResult> kongaResults = scrapeKonga(query);
//            repository.saveAll(kongaResults);
//            for (ProductResult product : kongaResults) {
//                if (product.getPrice().equals(targetPrice.toString())) {
//                    return List.of(product);
//                }
//            }

            // 5. Try Kusnap
            List<ProductResult> kusnapResults = scrapeKusnap(query);
            repository.saveAll(kusnapResults);
            for (ProductResult product : kusnapResults) {
                if (product.getPrice().equals(targetPrice.toString())) {
                    productResults.add(product);
                    return List.of(product);
                }
            }
        }

        double tolerance = 0.1; // 10%
        double min = targetPrice * (1 - tolerance);
        double max = targetPrice * (1 + tolerance);

        List<ProductResult> filtered = productResults.stream()
                .filter(p -> {
                    Double price = parsePrice(p.getPrice());
                    return price >= min && price <= max;
                })
                .sorted(Comparator.comparing(p -> parsePrice(p.getPrice())))
                .collect(Collectors.toList());

        return filtered;

        // 6. If all fail, return empty list
//        return productResults;
    }
}
