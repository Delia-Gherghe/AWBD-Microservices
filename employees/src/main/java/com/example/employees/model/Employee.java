package com.example.employees.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Employee extends RepresentationModel<Employee> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Last name must have a value!")
    @Size(max = 100, message = "Last name must have less than 100 characters!")
    private String lastName;

    @Size(max = 200, message = "First name must have less than 200 characters!")
    private String firstName;

    @NotBlank(message = "Email must have a value!")
    @Email(message = "Please enter a valid email!")
    private String email;

    @NotNull(message = "Hire date must have a value!")
    private LocalDate hireDate;

    @NotNull(message = "Salary must have a value!")
    @Min(value = 100, message = "Salary must be over 100!")
    private Double salary;

    public int chooseRaise(Raise raise){
        DateTime now = DateTime.now();
        int experience = Years.yearsBetween(DateTime.parse(hireDate.toString()), now).getYears();
        if (experience < 1){
            return raise.getUnderOne();
        } else if (experience < 3) {
            return raise.getUnderThree();
        } else if (experience < 5) {
            return raise.getUnderFive();
        } else {
            return raise.getOverFive();
        }
    }
}
