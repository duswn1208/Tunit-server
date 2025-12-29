package com.tunit.kafka;

import com.tunit.domain.lesson.service.LessonReserveProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReservationKafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ReservationKafkaConsumer.class);
    private final LessonReserveProcessorService lessonReserveProcessorService;

    @Autowired
    public ReservationKafkaConsumer(LessonReserveProcessorService lessonReserveProcessorService) {
        this.lessonReserveProcessorService = lessonReserveProcessorService;
    }

    @KafkaListener(topics = "reservation-test", groupId = "reservation-test-group")
    public void listen(String message) {
        logger.info("Received reservation message: {}", message);
        lessonReserveProcessorService.processReserveLesson(message);
    }
}
