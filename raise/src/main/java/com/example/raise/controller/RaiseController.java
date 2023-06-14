package com.example.raise.controller;

import com.example.raise.config.PropertiesConfig;
import com.example.raise.model.Raise;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RaiseController {
    private final PropertiesConfig config;
    private static final Logger logger = LoggerFactory.getLogger(RaiseController.class);

    public RaiseController(PropertiesConfig config) {
        this.config = config;
    }

    @GetMapping("/raise")
    public Raise getRaise(){
        logger.info("get raise ...");
        return new Raise(config.getUnderOne(), config.getUnderThree(), config.getUnderFive(), config.getOverFive());
    }
}
