package com.store.orderservice.controllers;

import com.store.orderservice.dto.OrderRequest;
import com.store.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Received Order Request: {}", orderRequest);
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }
}
