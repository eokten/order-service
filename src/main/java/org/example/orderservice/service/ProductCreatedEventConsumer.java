package org.example.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.event.api.IOnProductCreatedConsumerService;
import org.example.event.model.ProductCreatedPayload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductCreatedEventConsumer implements IOnProductCreatedConsumerService {

    @Override
    public void onProductCreated(ProductCreatedPayload payload, ProductCreatedPayloadHeaders headers) {
        log.info("Received product created [{}]", payload);
    }
}
