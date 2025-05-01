package com.gulcht.microservices.order.service;

import com.gulcht.microservices.order.client.InventoryClient;
import com.gulcht.microservices.order.dto.OrderRequest;
import com.gulcht.microservices.order.event.OrderPlacedEvent;
import com.gulcht.microservices.order.model.Order;
import com.gulcht.microservices.order.repository.OrderRepositoy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepositoy orderRepositoy;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        boolean isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if(isProductInStock){
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setQuantity(orderRequest.quantity());
            order.setSkuCode(orderRequest.skuCode());
            orderRepositoy.save(order);


            // Sending the message to kafka topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
            orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
            log.info("Email: {}", orderRequest.userDetails().email());
            log.info("FirstName: {}", orderRequest.userDetails().firstName());
            log.info("LastName: {}", orderRequest.userDetails().lastName());
            log.info("Start-Sending OrderPlacedEvent  {} to kafka topic: order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End-Sending OrderPlacedEvent  {} to kafka topic: order-placed", orderPlacedEvent);
        } else {
            throw new IllegalArgumentException("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
        }
    }
}
