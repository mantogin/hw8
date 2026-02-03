package ru.mantogin.hw8.order.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfiguration {

    @Bean
    public RestClient billingRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://billing-load-balancer:8001")
//                .baseUrl("http://localhost:8001")
                .build();
    }

    @Bean
    public RestClient warehouseRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://warehouse-load-balancer:8004")
//                .baseUrl("http://localhost:8004")
                .build();
    }

    @Bean
    public RestClient deliveryRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://delivery-load-balancer:8002")
//                .baseUrl("http://localhost:8002")
                .build();
    }
}
