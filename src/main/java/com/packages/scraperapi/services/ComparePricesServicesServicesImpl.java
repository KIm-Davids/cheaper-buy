package com.packages.scraperapi.services;

import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
import com.packages.scraperapi.utilities.BestMatchingProductResultImpl;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ComparePricesServicesServicesImpl implements ComparePricesServicesInterface {

    @Override
    public ProductResult scrapeJumia(Query query) {
        try {
            String searchUrl = "https://www.jumia.com.ng/catalog/?q=" + URLEncoder.encode(query.getQuery(), "UTF-8");
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla")
                    .timeout(10_000)
                    .get();

            Elements products = doc.select("article.prd");
            String cheapestTitle = "Not Found";
            String cheapestPrice = "N/A";
            String cheapestLink = "#";
            double lowestPrice = Double.MAX_VALUE;

            for(Element product : products){
                try {
                    String priceText = product.selectFirst(".prc").text();
                    String title = product.selectFirst("h3").text();
//                    String link = product.selectFirst("a").attr("href");
                    Element anchor = product.selectFirst("a.core");
                    String href = (anchor != null) ? anchor.attr("href") : "#";
                    String link = href.startsWith("http") ? href : "https://www.jumia.com.ng" + href;

                    String clean = priceText.replaceAll("[^\\d]", "");
                    double price = Double.parseDouble(clean);
                    System.out.println(price);
                    System.out.println(cheapestPrice);

                    if (price < lowestPrice) {
                        lowestPrice = price;
                        cheapestTitle = title;
                        cheapestPrice = priceText;
                        cheapestLink = link;
                    }
                }catch (Exception e){
                    continue;
                }
            }
            return new ProductResult("Jumia", cheapestTitle, cheapestPrice, cheapestLink);

        }catch(Exception e){
            return new ProductResult("Jumia", "Not Found", "N/A", "#");
        }
    }

    @Override
    public ProductResult scrapeKonga(Query query) {
        String title = "";
        String price = "";
        String link = "";
        try {
//            String url = "https://www.konga.com/search?search=" + URLEncoder.encode(query, "UTF-8");
//            Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
//            Element product = doc.selectFirst("div.cca3");
//            String title = product.selectFirst(".af885_1iPzH").text();
//            String price = product.selectFirst(".d7c0f_sjaQl").text();
//            String link = "https://www.konga.com" + product.selectFirst("a").attr("href");
//
//            return new ProductResult("Konga", title, price, link);

            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            WebDriver driver = new ChromeDriver(options);

            driver.get("https://www.konga.com/search?search=" + URLEncoder.encode(query.getQuery(), "UTF-8"));
            Thread.sleep(3000);

            String html = driver.getPageSource();
            Document doc = Jsoup.parse(html);

            Elements products = doc.select("div.ListingComponent_salesAIAdsCarouselItem__Tjbn0");

            for(Element product : products){
                title = product.selectFirst("h3.RecommendationsCard_recCardProductName___xN6_").text();
                price = product.selectFirst("span.shared_price__gnso_").text();
                String href = product.selectFirst("a").attr("href");
                link = href.startsWith("http") ? href : "https://www.konga.com" + href;
            }



            System.out.println("Title: " + title);
            System.out.println("Price: " + price);
            System.out.println("Link: " + link);

//            WeDriver driver = new ChromeDriver();
//            driver.get("https://www.konga.com/search?search=" + query);
//            String renderedHtml = driver.getPageSource();
//            Document doc = Jsoup.parse(renderedHtml);
//
//            Element product = doc.selectFirst("div");
            driver.quit();

            return new ProductResult("Konga", title, price, link);
        } catch (Exception e) {
            return new ProductResult("Konga", e.getMessage(), "N/A", "#");
        }
    }

    @Override
    public ProductResult scrapeAliexpress(Query query) {
        return null;
    }

    @Override
    public ProductResult scrapeAmazon(Query query) {
        return null;
    }

    @Override
    public List<ProductResult> scrapeJiji(Query query) {
        String title = "";
        String price = "";
        String link = "";

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        String searchUrl = "https://jiji.ng/search?query=" + query.getQuery().replace(" ", "+");

        driver.get(searchUrl);

        // Let the page load completely (you can add waits for better reliability)
        try {
            Thread.sleep(5000); // Wait 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String pageSource = driver.getPageSource();
        driver.quit();

        Document doc = Jsoup.parse(pageSource);
        Elements products = doc.select("div.b-list-advert-base__data__inner");
        List<ProductResult> productResultList = new ArrayList<>();

        for (Element product : products) {
            title = product.select(".b-advert-title-inner.qa-advert-title").text();
            price = product.select(".qa-advert-price").text();
            String description = product.select(".b-list-advert-base__description-text").text();
            productResultList.add(new ProductResult("jiji",title, price,description));

            System.out.println("Title: " + title);
            System.out.println("Price: " + price);
            System.out.println("Description: " + description);
            System.out.println("------");
        }

        BestMatchingProductResultImpl bestMatchingProductResult = new BestMatchingProductResultImpl();

        //        products.stream()
//                .sorted((a, b) -> extractNumericPrice(a.price) - extractNumericPrice(b.price))
//                .limit(1)
//                .forEach(p -> {
//                    System.out.println("Title: " + p.title);
//                    System.out.println("Price: " + p.price);
//                    System.out.println("Link: " + p.link);
//                });
        return bestMatchingProductResult.getBestMatchingTitles(query.getQuery(), productResultList);
    }

    private static int extractNumericPrice(String priceText) {
        try {
            String numeric = priceText.replaceAll("[^\\d]", "");
            return Integer.parseInt(numeric);
        } catch (Exception e) {
            return Integer.MAX_VALUE; // In case of parse failure
        }
    }
}
