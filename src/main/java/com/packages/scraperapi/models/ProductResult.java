package com.packages.scraperapi.models;

public class ProductResult {
    public String platform;
    public String title;
    public String price;
    public String link;


    public ProductResult(String platform, String title, String price, String link) {
        this.platform = platform;
        this.title = title;
        this.price = price;
        this.link = link;
    }

    @Override
    public String toString() {
        return "ProductResult{" +
                "platform='" + platform + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
