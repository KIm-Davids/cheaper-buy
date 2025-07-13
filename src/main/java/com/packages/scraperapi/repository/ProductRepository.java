package com.packages.scraperapi.repository;


import com.packages.scraperapi.models.ProductResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductResult, Long> {

}
