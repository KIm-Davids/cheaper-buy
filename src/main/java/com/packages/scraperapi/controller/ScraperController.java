package com.packages.scraperapi.controller;

import java.util.ArrayList;
import java.util.List;

import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
import com.packages.scraperapi.services.ComparePricesServicesImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


public class ScraperController {

    private ComparePricesServicesImpl services;

    private ScraperController(ComparePricesServicesImpl services){
        this.services = services;
    }

    @GetMapping("/api/compare")
    public List<ProductResult> comparePrice(@RequestParam Query query){

        return null;
    }
}
