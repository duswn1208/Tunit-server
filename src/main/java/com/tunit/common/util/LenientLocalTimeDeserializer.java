package com.tunit.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * "HH:mm" 과 "HH:mm:ss" 두 포맷 모두 수용하는 LocalTime 디시리얼라이저.
 * 프론트가 응답 LocalTime을 echo 할 때 포맷 차이로 인한 400 방지용.
 */
public class LenientLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter HHMMSS = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(value, HHMM);
        } catch (DateTimeParseException ignored) {
            return LocalTime.parse(value, HHMMSS);
        }
    }
}
