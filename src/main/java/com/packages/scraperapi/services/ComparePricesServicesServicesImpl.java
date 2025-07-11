package com.packages.scraperapi.services;

import com.microsoft.playwright.*;
import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
//import com.packages.scraperapi.utilities.BestMatchingProductResultImpl;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparePricesServicesServicesImpl implements ComparePricesServicesInterface {

    @Override
    public List<ProductResult> scrapeJiji(Query query) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        List<ProductResult> productResultList = new ArrayList<>();
        int page = 1;
        int maxPages = 5;

        while (page <= maxPages) {
            String searchUrl = "https://jiji.ng/search?query=" + query.getQuery().replace(" ", "+") + "&page=" + page;
            driver.get(searchUrl);

            try {
                Thread.sleep(4000); // Let the page load
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

                System.out.println("title" + title);
                System.out.println("price" + price);
                System.out.println("image" + imageUrl);
                System.out.println("description" + description);

                double numericPrice = extractNumericPrice(price);
                if (numericPrice >= query.getBudgetAmount()) {
                    ProductResult result = new ProductResult(
                            "jiji", title, price, fullLink, imageUrl, description
                    );
                    productResultList.add(result);
                }
            }



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
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        List<ProductResult> productList = new ArrayList<>();
        int page = 1;
        int maxPages = 5;

        while (page <= maxPages) {
            String searchUrl = "https://www.jumia.com.ng/catalog/?q=" + query.getQuery().replace(" ", "+") + "&page=" + page;
            driver.get(searchUrl);

            try {
                Thread.sleep(5000); // Wait for page to load
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


                    System.out.println("title " + title);
                    System.out.println("price " + price);
                    System.out.println("link " + link);
                    System.out.println("image " + imageUrl);
                    System.out.println("description: ");

                    double numericPrice = extractNumericPrice(price);
                    if (numericPrice <= query.getBudgetAmount()) {
                        productList.add(new ProductResult("jumia", title, price, link, imageUrl, ""));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            page++;
        }

        driver.quit();

        // Sort results by price
        productList.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));

        return productList;
    }

//    @Override
//    public List<ProductResult> scrapeKonga(Query query) {
//        WebDriverManager.chromedriver().setup();
//        WebDriver driver = new ChromeDriver();
//        List<ProductResult> results = new ArrayList<>();
//        int page = 1;
//        int maxPages = 5;
//
//        while (page <= maxPages) {
//            String searchUrl = "https://www.konga.com/search?search=" + URLEncoder.encode(query.getQuery(), StandardCharsets.UTF_8) + "&page=" + page;
//            driver.get(searchUrl);
//
//            try {
//                Thread.sleep(5000); // Wait for page to load
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            Document doc = Jsoup.parse(driver.getPageSource());
//            Elements products = doc.select("li.List_listItem__KlvU2");
//
//            if (products.isEmpty()) break;
//
//            for (Element product : products) {
//                try {
//                    // Extract title
//                    String title = product.select("h3.ListingCard_productTitle__9Kzxv").text();
//
//                    // Extract price
//                    String price = product.select("span.shared_initialPrice__cTRSe").text();
//
//                    // Extract link
//                    String relativeLink = product.select("a").attr("href");
//                    String fullLink = "https://www.konga.com" + relativeLink;
//
//                    // Extract image
//                    Element img = product.selectFirst("img");
//                    String imageUrl = img != null ? img.attr("src") : "Image not found";
//
//                    System.out.println("Title: " + title);
//                    System.out.println("Price: " + price);
//                    System.out.println("Link: " + fullLink);
//                    System.out.println("Image URL: " + imageUrl);
//                    System.out.println("----------------------------------------");
//
//                    double numericPrice = extractNumericPrice(price);
//                    if (numericPrice <= query.getBudgetAmount()) {
//                        results.add(new ProductResult("konga", title, fullLink, imageUrl, price, ""));
//                    }
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            page++;
//        }
//
//        driver.quit();
//
//        // Sort results by price
//        results.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));
//
//        return results;
//    }
    public List<ProductResult> scrapeKonga(Query query) {

        List<ProductResult> results = new ArrayList<>();
        int page = 1;
        int maxPages = 5;

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page pageObj = context.newPage();

            while (page <= maxPages) {
                String searchUrl = "https://www.konga.com/search?search=" + URLEncoder.encode(query.getQuery(), StandardCharsets.UTF_8) + "&page=" + page;
                pageObj.navigate(searchUrl);

                // Wait for page to load
                pageObj.waitForSelector("li.List_listItem__KlvU2");

                // Parse with Jsoup
                String content = pageObj.content();
                Document doc = Jsoup.parse(content);
                Elements products = doc.select("li.List_listItem__KlvU2");

                if (products.isEmpty()) break;

                for (Element product : products) {
                    try {
                        String title = product.select("h3.ListingCard_productTitle__9Kzxv").text();
                        String price = product.select("span.shared_initialPrice__cTRSe").text();
                        String relativeLink = product.select("a").attr("href");
                        String fullLink = "https://www.konga.com" + relativeLink;

                        Element img = product.selectFirst("img");
                        String imageUrl = img != null ? img.attr("src") : "Image not found";

                        System.out.println("Title: " + title);
                        System.out.println("Price: " + price);
                        System.out.println("Link: " + fullLink);
                        System.out.println("Image URL: " + imageUrl);
                        System.out.println("----------------------------------------");

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

            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        results.sort(Comparator.comparingDouble(p -> extractNumericPrice(p.getPrice())));
        return results;
    }
    @Override
    public ProductResult scrapeAliexpress(Query query) {
        return null;
    }

    @Override
    public ProductResult scrapeAmazon(Query query) {
        return null;
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



    private static int extractNumericPrice(String priceText) {
        try {
            String numeric = priceText.replaceAll("[^\\d]", "");
            return Integer.parseInt(numeric);
        } catch (Exception e) {
            return Integer.MAX_VALUE; // In case of parse failure
        }
    }
}
