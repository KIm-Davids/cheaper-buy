package com.packages.scraperapi.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ProductResult {
    @Id
    private Long id;
    private String platform;
    private String title;
    private String price;
    private String link;
    private String image;
    private String description;

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

    public String getImage() {
        return image;
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
