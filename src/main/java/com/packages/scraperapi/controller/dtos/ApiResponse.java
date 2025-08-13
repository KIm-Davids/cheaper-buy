package com.packages.scraperapi.controller.dtos;

import com.packages.scraperapi.models.ProductResult;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ApiResponse {
    public boolean isLoaded;
    public List<ProductResult> data;
    public Object response;

    public ApiResponse(boolean b, List<ProductResult> response) {
        this.isLoaded = b;
        this.data = response;
    }

    public ApiResponse(boolean b, Object response) {
        this.isLoaded = b;
        this.response = response;
    }
}
