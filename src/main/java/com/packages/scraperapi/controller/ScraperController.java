//package com.packages.scraperapi.controller;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.packages.scraperapi.models.ProductResult;
//import com.packages.scraperapi.services.ComparePricesServicesServicesImpl;
//
//
//public class ScraperController {
//
//    private ComparePricesServicesServicesImpl services;
//
//    private ScraperController(ComparePricesServicesServicesImpl services){
//        this.services = services;
//    }
//
//    @GetMapping("/api/compare")
//    public List<ProductResult> comparePrice(@RequestParam String query){
//        List<ProductResult> results = new ArrayList<>();
//
//        results.add(services.scrapeJumia(query));
//        results.add(services.scrapeKonga(query));
//        results.add(services.scrapeAliexpress(query));
//        results.add(services.scrapeAmazon(query));
//
//        return results;
//    }
//}
