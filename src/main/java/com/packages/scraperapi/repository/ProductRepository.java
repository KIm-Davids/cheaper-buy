package com.packages.scraperapi.repository;

import com.packages.scraperapi.models.ProductResult;

import java.util.List;

public interface ProductRepository  {
    List<ProductResult> getProductResult();
}
