package com.packages.scraperapi.services;

import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;

import java.util.List;

public interface ComparePricesServicesInterface {
    ProductResult scrapeJumia(Query query);
    ProductResult scrapeKonga(Query query);
    ProductResult scrapeAliexpress(Query query);
    ProductResult scrapeAmazon(Query query);
    List<ProductResult> scrapeJiji(Query query);
}
