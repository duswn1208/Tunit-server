package com.tunit.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    public void processReservation(String message) {
        // 실제 예약 비즈니스 로직 구현
        logger.info("Processing reservation: {}", message);
        // TODO: 예약 DB 저장 등 추가 구현
    }
}

