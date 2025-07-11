package com.packages.scraperapi.services;

import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;

import java.util.List;

public interface ComparePricesServicesInterface {
    List<ProductResult> scrapeJumia(Query query);
    List<ProductResult> scrapeKonga(Query query);
    ProductResult scrapeAliexpress(Query query);
    ProductResult scrapeAmazon(Query query);
    List<ProductResult> scrapeJiji(Query query);
}
