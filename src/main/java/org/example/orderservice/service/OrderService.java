package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.dto.OrderCreatedEvent;
import org.example.orderservice.dto.OrderDto;
import org.example.orderservice.model.Order;
import org.example.rest.ProductApi;
import org.example.rest.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);
    private static final AtomicLong ORDER_ID = new AtomicLong(0);
    private static final Map<Long, Order> ORDERS = new ConcurrentHashMap<>();

    private final ProductApi productApi;

    private final OrderCreatedEventProducer orderCreatedEventProducer;

    public OrderDto createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setId(ORDER_ID.getAndIncrement());
        order.setTimestamp(System.currentTimeMillis());

        List<ProductDto> products = productApi.getProducts();

        List<ProductDto> existingProducts = orderDto
                .getProducts()
                .stream()
                .flatMap(productName -> products.stream().filter(productDto -> Objects.equals(productDto.getName(), productName)))
                .toList();

        order.setProductIds(existingProducts.stream().map(ProductDto::getId).toList());

        ORDERS.put(order.getId(), order);

//        order.getProductIds().forEach(orderCreatedEventProducer::produceOrderCreatedEvent);
        orderCreatedEventProducer.produceOrderCreatedEvent(new OrderCreatedEvent(order.getId(), order.getProductIds()));

        return new OrderDto()
                .setId(order.getId())
                .setTimestamp(order.getTimestamp())
                .setProducts(existingProducts.stream().map(ProductDto::getName).toList());
    }

    public void submitOrder(OrderDto orderDto) {
        EXECUTOR_SERVICE.execute(() -> {
            log.info("Submitting new order...");
            OrderDto order = createOrder(orderDto);
            log.info("Order created: " + order);
        });
    }

    public List<OrderDto> getOrders() {
        return ORDERS
                .values()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private OrderDto toDto(Order order) {
        List<ProductDto> products = productApi.getProducts();

        List<String> existingProductNames = order
                .getProductIds()
                .stream()
                .flatMap(productId -> products.stream().filter(productDto -> Objects.equals(productDto.getId(), productId)))
                .map(ProductDto::getName)
                .toList();

        return new OrderDto()
                .setId(order.getId())
                .setTimestamp(order.getTimestamp())
                .setProducts(existingProductNames);
    }
}
