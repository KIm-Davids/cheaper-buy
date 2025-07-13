package com.packages.scraperapi.services;

import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ComparePricesServicesInterface {
    List<ProductResult> scrapeJumia(Query query);
    List<ProductResult> scrapeKonga(Query query);
    List<ProductResult> scrapeKusnap(Query query);
    List<ProductResult> scrapeJiji(Query query);
}
