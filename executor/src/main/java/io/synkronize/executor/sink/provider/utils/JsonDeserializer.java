package io.synkronize.executor.sink.provider.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JsonDeserializer {

    private final ObjectMapper objectMapper;

    public JsonDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new DeserializationError("An error occurred while deserializing JSON: " + e.getMessage(), e);
        }
    }

    public static class DeserializationError extends RuntimeException {
        public DeserializationError(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
