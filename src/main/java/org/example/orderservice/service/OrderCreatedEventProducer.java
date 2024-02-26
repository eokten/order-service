package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.dto.OrderCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedEventProducer {

    @Value("${spring.kafka.producer.topic}")
    private String orderCreatedEventsTopic;

//    private final KafkaTemplate<Integer, String> kafkaTemplate;
//
//    public void produceOrderCreatedEvent(Long productId) {
//        log.info("Send order created event {}", productId);
//        kafkaTemplate.send(orderCreatedEventsTopic, productId.toString());
//    }

    private final KafkaTemplate<Integer, OrderCreatedEvent> kafkaTemplate;

    public void produceOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Sending {}", event);
        kafkaTemplate.send(orderCreatedEventsTopic, event);
    }
}
