package com.tunit.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class ReservationKafkaTestController {
    private final ReservationKafkaProducer producer;

    @Autowired
    public ReservationKafkaTestController(ReservationKafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> sendReservation(@RequestBody String message) {
        producer.sendReservationRequest(message);
        return ResponseEntity.ok("Reservation message sent: " + message);
    }
    @GetMapping("/reserve")
    public String reserve(@RequestParam("msg") String msg) {
        producer.sendReservationRequest(msg);
        return "요청 보냄: " + msg;
    }
}

