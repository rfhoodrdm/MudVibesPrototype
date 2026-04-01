package com.example.mudvibe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Configuration
@ConfigurationProperties(prefix = "text-formatting")
@Slf4j
public class TextFormattingConfig {

    /**
     * Maximum characters allowed on a line of room description text before inserting a newline.
     */
    private int roomDescriptionLineLength = 120;
    
    
    @PostConstruct
    public void init() {
    	log.info("Text formatting config properties: {}", this.toString());
    }
}
