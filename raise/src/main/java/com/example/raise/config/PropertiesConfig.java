package com.example.raise.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("percentage")
@Getter
@Setter
public class PropertiesConfig {
    private int underOne;
    private int underThree;
    private int underFive;
    private int overFive;
}
