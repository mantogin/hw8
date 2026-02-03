package ru.mantogin.hw8.delivery.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppController {

    private boolean[] reservedDeliveryTime = new boolean[8];

    @PutMapping(value = "/delivery/time/{slot}")
    public ResponseEntity<Void> changeDeliveryTimeSlot(@PathVariable int slot, @RequestBody boolean status) {

        if ((slot < 0 && slot >= reservedDeliveryTime.length) || (status & reservedDeliveryTime[slot])) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        reservedDeliveryTime[slot] = status;

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/delivery/time/{slot}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDeliveryTimeSlotStatus(@PathVariable int slot) {

        if ((slot < 0 && slot >= reservedDeliveryTime.length)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        String status = reservedDeliveryTime[slot] ? "busy" : "free";

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
