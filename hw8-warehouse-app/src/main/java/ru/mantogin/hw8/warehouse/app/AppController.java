package ru.mantogin.hw8.warehouse.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AppController {

    private Map<String, Integer> products = new HashMap<>();

    private Map<Integer, Integer> reservations = new HashMap<>();

    private Integer reservationNumber = 0;

    @PostMapping(value = "/warehouse/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addProduct(@RequestBody String name) {

        if (products.containsKey(name)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        products.put(name, 0);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/warehouse/products/{name}")
    public ResponseEntity<Integer> changeProductQuantity(@PathVariable String name, @RequestBody Integer quantity) {

        Integer currentProductQuantity = products.get(name);
        Integer newQuantity = currentProductQuantity + quantity;
        if (newQuantity < 0) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        reservations.put(++reservationNumber, quantity);
        products.put(name, newQuantity);

        return new ResponseEntity<Integer>(reservationNumber, HttpStatus.OK);
    }

    @DeleteMapping(value = "/warehouse/products/{productName}/reservations/{reservationNumber}")
    public ResponseEntity<Void> rollbackProductReservation(@PathVariable String productName,
                                                           @PathVariable Integer reservationNumber) {

        if (!products.containsKey(productName)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (reservations.containsKey(reservationNumber)) {
            Integer reservedQuantity = reservations.get(reservationNumber);
            if (reservedQuantity != 0) {
                Integer currentQuantity = products.get(productName);
                Integer newQuantity = currentQuantity + (-reservedQuantity);
                products.put(productName, newQuantity);
                reservations.put(reservationNumber, 0);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/warehouse/products/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getProductQuantity(@PathVariable String name) {

        Integer quantity = products.get(name);

        return new ResponseEntity<>(quantity, HttpStatus.OK);
    }
}
