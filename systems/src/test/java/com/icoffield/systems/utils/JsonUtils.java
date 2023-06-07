package com.icoffield.systems.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    public static String asJsonString(ObjectMapper objectMapper, Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
