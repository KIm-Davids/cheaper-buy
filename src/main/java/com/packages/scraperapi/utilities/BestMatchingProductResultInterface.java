package com.packages.scraperapi.utilities;

import com.packages.scraperapi.models.ProductResult;
import org.jsoup.select.Elements;

import java.util.List;

public interface BestMatchingProductResultInterface {
    List<ProductResult>  getBestMatchingTitles(String query, List<ProductResult> products);
}
