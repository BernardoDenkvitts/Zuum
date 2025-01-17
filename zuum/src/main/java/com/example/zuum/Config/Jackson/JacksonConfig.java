package com.example.zuum.Config.Jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.locationtech.jts.geom.Point;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        
        module.addDeserializer(Point.class, new PointDeserializer());
        module.addSerializer(Point.class, new PointSerializer());
        
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
