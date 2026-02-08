package dev.gertjanassies.filament.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
    public static String toJson(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (Exception e) {
            return "Failed to serialize to JSON: " + e.getMessage();
        }
    }
}
