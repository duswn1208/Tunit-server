package com.tunit.domain.lesson.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.kafka.ReserveKafkaType;
import com.tunit.kafka.ReservationKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonReserveService {
    private static final Logger log = LoggerFactory.getLogger(LessonReserveService.class);
    private final ReservationKafkaProducer reservationKafkaProducer;

    public void reserveLessonKafka(Long userNo, LessonReserveSaveDto dto) {
        sendKafkaMessage(ReserveKafkaType.CREATE, userNo, null, dto, null);
    }

    public void rescheduleLessonKafka(Long userNo, Long lessonReservationNo, LessonReserveSaveDto dto) {
        sendKafkaMessage(ReserveKafkaType.RESCHEDULE, userNo, lessonReservationNo, dto, null);
    }

    public void cancelLessonKafka(Long userNo, Long lessonReservationNo) {
        sendKafkaMessage(ReserveKafkaType.CANCEL, userNo, lessonReservationNo, null, null);
    }

    public void createLessonKafka(Long tutorProfileNo, LessonReserveSaveDto dto) {
        sendKafkaMessage(ReserveKafkaType.TUTOR_CREATE, null, null, dto, tutorProfileNo);
    }

    private void sendKafkaMessage(ReserveKafkaType type, Long userNo, Long lessonReservationNo, LessonReserveSaveDto dto, Long tutorProfileNo) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KafkaReserveMessage message = new KafkaReserveMessage(type, userNo, lessonReservationNo, dto, tutorProfileNo);
            String json = objectMapper.writeValueAsString(message);
            reservationKafkaProducer.sendReservationRequest(json);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 변환 오류", e);
            throw new RuntimeException("Kafka 메시지 변환 오류: " + e.getMessage());
        }
    }

    public static class KafkaReserveMessage {
        public ReserveKafkaType type;
        public Long userNo;
        public Long lessonReservationNo;
        public LessonReserveSaveDto lessonReserveSaveDto;
        public Long tutorProfileNo;

        public KafkaReserveMessage(ReserveKafkaType type, Long userNo, Long lessonReservationNo, LessonReserveSaveDto lessonReserveSaveDto, Long tutorProfileNo) {
            this.type = type;
            this.userNo = userNo;
            this.lessonReservationNo = lessonReservationNo;
            this.lessonReserveSaveDto = lessonReserveSaveDto;
            this.tutorProfileNo = tutorProfileNo;
        }
    }

}
