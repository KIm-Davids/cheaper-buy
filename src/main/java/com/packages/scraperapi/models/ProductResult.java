package com.packages.scraperapi.models;

public class ProductResult {
    public String platform;
    public String title;
    public String price;
    public String link;
    public String image;
    public String description;



    public String getImage() {
        return image;
    }

    public ProductResult(String platform, String title, String price, String link, String image, String description) {
        this.platform = platform;
        this.title = title;
        this.price = price;
        this.link = link;
        this.image = image;
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProductResult{" +
                "platform='" + platform + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getPlatform() {
        return platform;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }
}
