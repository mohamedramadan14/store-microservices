package com.store.productservice.controllers;

import com.store.productservice.dto.ProductRequest;
import com.store.productservice.dto.ProductResponse;
import com.store.productservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)

    public void createProduct(@RequestBody ProductRequest productRequest) {
        log.info("Received request to create product {}", productRequest);
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        log.info("Received request to get all products");
        return productService.getAllProducts();
    }
}
