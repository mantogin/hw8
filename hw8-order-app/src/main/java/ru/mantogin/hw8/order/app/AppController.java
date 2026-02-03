package ru.mantogin.hw8.order.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
public class AppController {

    private record Order(Integer accountNumber,
                         Integer amount,
                         String productName,
                         Integer productQuantity,
                         Integer deliveryTimeSlot) {}

    @Autowired
    @Qualifier("billingRestClient")
    private RestClient billingRestClient;

    @Autowired
    @Qualifier("warehouseRestClient")
    private RestClient warehouseRestClient;

    @Autowired
    @Qualifier("deliveryRestClient")
    private RestClient deliveryRestClient;

    private Integer withdrawMoneyFromAccount(Integer accountNumber, Integer amount) {

        ResponseEntity<Integer> response = billingRestClient
                .put()
                .uri("/billing/account/{number}", accountNumber)
                .body(-amount)
                .retrieve()
                .toEntity(Integer.class);

        return response.getBody();
    }

    private void rollbackBillingTransaction(Integer accountNumber, Integer transactionNumber) {

        // Откатить платеж в Биллинге
        billingRestClient
                .delete()
                .uri("/billing/account/{accountNumber}/transactions/{transactionNumber}"
                        , accountNumber
                        , transactionNumber)
                .retrieve()
                .toEntity(Void.class);
    }

    private Integer reserveProductInWarehouse(String productName, Integer productQuantity) {

        ResponseEntity<Integer> response = warehouseRestClient
                .put()
                .uri("/warehouse/products/{name}", productName)
                .body(-productQuantity)
                .retrieve()
                .toEntity(Integer.class);

        return response.getBody();
    }

    private void rollbackWarehouseReserve(String productName, Integer reserveNumber) {

        // Окатить резервирование продукта на Складе
        warehouseRestClient
                .delete()
                .uri("/warehouse/products/{productName}/reservations/{reservationNumber}"
                        , productName
                        , reserveNumber )
                .retrieve()
                .toEntity(Void.class);
    }

    private void reserveDelivery(Integer timeSlot) {

        deliveryRestClient
                .put()
                .uri("/delivery/time/{timeSlot}", timeSlot)
                .body(true)
                .retrieve()
                .toEntity(Void.class);
    }

    @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createOrder(@RequestBody Order order) {

        Integer billingTransactionNumber;
        try {
            billingTransactionNumber = withdrawMoneyFromAccount(order.accountNumber, order.amount);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Integer warehouseReserveNumber;
        try {
            warehouseReserveNumber = reserveProductInWarehouse(order.productName, order.productQuantity);
        } catch (Exception e) {

            try {
                rollbackBillingTransaction(order.accountNumber, billingTransactionNumber);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        try {
            reserveDelivery(order.deliveryTimeSlot);
        } catch (Exception e) {

            try {
                rollbackBillingTransaction(order.accountNumber, billingTransactionNumber);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                rollbackWarehouseReserve(order.productName, warehouseReserveNumber);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
