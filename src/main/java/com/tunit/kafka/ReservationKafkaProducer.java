package com.tunit.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservationKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(ReservationKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "reservation-test";

    @Autowired
    public ReservationKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReservationRequest(String message) {
        kafkaTemplate.send(TOPIC, message).whenComplete((result, ex) -> {
            if (ex != null) {
                System.out.println("Failed to send message: " + ex.getMessage());
            } else {
                System.out.println("Message sent successfully to topic " + result.getRecordMetadata().topic() +
                        " partition " + result.getRecordMetadata().partition() +
                        " offset " + result.getRecordMetadata().offset());
            }
        });
    }
}

