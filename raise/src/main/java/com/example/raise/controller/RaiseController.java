package com.example.raise.controller;

import com.example.raise.config.PropertiesConfig;
import com.example.raise.model.Raise;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RaiseController {
    private final PropertiesConfig config;

    public RaiseController(PropertiesConfig config) {
        this.config = config;
    }

    @GetMapping("/raise")
    public Raise getRaise(){
        return new Raise(config.getUnderOne(), config.getUnderThree(), config.getUnderFive(), config.getOverFive());
    }
}
