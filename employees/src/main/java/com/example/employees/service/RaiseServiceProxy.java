package com.example.employees.service;

import com.example.employees.model.Raise;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "raise")
public interface RaiseServiceProxy {
    @GetMapping(value = "/raise", consumes = "application/json")
    Raise findRaise();
}
