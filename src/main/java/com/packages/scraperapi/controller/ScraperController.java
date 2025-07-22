package com.packages.scraperapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.packages.scraperapi.controller.dtos.ApiResponse;
import com.packages.scraperapi.models.ProductResult;
import com.packages.scraperapi.models.Query;
import com.packages.scraperapi.services.ComparePricesServicesImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://smartpriceng.vercel.app")
public class ScraperController {

    private ComparePricesServicesImpl services;

    private ScraperController(ComparePricesServicesImpl services){
        this.services = services;
    }

    @PostMapping("/compare")
    public ResponseEntity<?> queryEndpoint(@RequestBody Query query){
        try{
            List<ProductResult> response = services.handleSearchQueries(query);
//            List<ProductResult> response = List.of(
//                    new ProductResult("1", "Test iPhone", "250000", "https://example.com", "image.jpg", "Mock description")
//            );
            return new ResponseEntity<>(new ApiResponse(true,response), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
