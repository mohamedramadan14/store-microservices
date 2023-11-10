package com.store.orderservice.services;

import com.store.orderservice.dto.InventoryResponse;
import com.store.orderservice.dto.OrderLineItemsDTO;
import com.store.orderservice.dto.OrderRequest;
import com.store.orderservice.models.Order;
import com.store.orderservice.models.OrderLineItems;
import com.store.orderservice.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest
                .getOrderLineItemsDTOList()
                .stream()
                .map(this::mapToDTO)
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponseForSkuCodes = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventory" ,
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if(inventoryResponseForSkuCodes == null) {
            log.warn("Null from Inventory Service");
            throw new IllegalArgumentException("all products in current order are out of stock");
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponseForSkuCodes)
                .allMatch(InventoryResponse::getIsInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
            log.info("Order with number {} placed successfully" , order.getOrderNumber());
        }else {
            log.warn("Not all products in stock");
            throw new IllegalArgumentException("Not all products in stock");
        }
    }

    private OrderLineItems mapToDTO(OrderLineItemsDTO orderLineItemsDTO) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDTO.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDTO.getPrice());
        orderLineItems.setQuantity(orderLineItemsDTO.getQuantity());
        return orderLineItems;
    }
}
