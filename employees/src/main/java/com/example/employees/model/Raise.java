package com.example.employees.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Raise {
    private int underOne;
    private int underThree;
    private int underFive;
    private int overFive;
}