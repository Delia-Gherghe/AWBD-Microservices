package com.example.employees.controller;

import com.example.employees.model.Employee;
import com.example.employees.model.Raise;
import com.example.employees.service.EmployeeService;
import com.example.employees.service.RaiseServiceProxy;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees", description = "Operations available for employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    private final RaiseServiceProxy raiseServiceProxy;

    public EmployeeController(EmployeeService employeeService, RaiseServiceProxy raiseServiceProxy) {
        this.employeeService = employeeService;
        this.raiseServiceProxy = raiseServiceProxy;
    }

    @GetMapping
    @Operation(summary = "Get all the employees from the database")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all employees")
    public CollectionModel<Employee> findAll(){
        List<Employee> employees = employeeService.getAll();
        for (final Employee employee : employees){
            Link selfLink = linkTo(methodOn(EmployeeController.class).getById(employee.getId())).withSelfRel();
            Link deleteLink = linkTo(methodOn(EmployeeController.class)
                    .deleteById(employee.getId())).withRel("deleteEmployee");
            employee.add(selfLink, deleteLink);
        }
        return CollectionModel.of(employees);
    }

    @GetMapping("/email")
    @Operation(summary = "Get employee with the given email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee by email"),
            @ApiResponse(responseCode = "404", description = "Employee with given email not found")
    })
    @CircuitBreaker(name = "raiseByEmail", fallbackMethod = "getByEmailFallback")
    public ResponseEntity<Employee> getByEmail(@RequestParam @Parameter(description = "employee email") String email){
        Employee employee = employeeService.findByEmail(email);
        Link selfLink = linkTo(methodOn(EmployeeController.class).getById(employee.getId())).withSelfRel();
        Link deleteLink = linkTo(methodOn(EmployeeController.class)
                .deleteById(employee.getId())).withRel("deleteEmployee");
        employee.add(selfLink, deleteLink);
        Raise raise = raiseServiceProxy.findRaise();
        employee.setSalary(employee.getSalary() * (100 + employee.chooseRaise(raise))/100);
        return ResponseEntity.ok().body(employee);
    }

    private ResponseEntity<Employee> getByEmailFallback(String email, Throwable throwable){
        Employee employee = employeeService.findByEmail(email);
        Link selfLink = linkTo(methodOn(EmployeeController.class).getById(employee.getId())).withSelfRel();
        Link deleteLink = linkTo(methodOn(EmployeeController.class)
                .deleteById(employee.getId())).withRel("deleteEmployee");
        employee.add(selfLink, deleteLink);
        return ResponseEntity.ok().body(employee);
    }


    @PostMapping
    @Operation(summary = "Add a new employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added employee"),
            @ApiResponse(responseCode = "400", description = "Invalid input for certain fields"),
            @ApiResponse(responseCode = "404", description = "The given email is already registered for another employee")
    })
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee){
        Employee savedEmployee = employeeService.add(employee);
        Link deleteLink = linkTo(methodOn(EmployeeController.class)
                .deleteById(savedEmployee.getId())).withRel("deleteEmployee");
        savedEmployee.add(deleteLink);
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{employeeId}").buildAndExpand(savedEmployee.getId()).toUri();
        return ResponseEntity.created(locationUri).body(savedEmployee);
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "Delete employee with a given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted employee"),
            @ApiResponse(responseCode = "404", description = "Employee with given id does not exist")
    })
    public ResponseEntity<Employee> deleteById(@PathVariable @Parameter(description = "Employee id") Long employeeId){
        return ResponseEntity.ok().body(employeeService.deleteById(employeeId));
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Retrieve employee with a given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee"),
            @ApiResponse(responseCode = "404", description = "Employee with given id does not exist")
    })
    public ResponseEntity<Employee> getById(@PathVariable @Parameter(description = "Employee id") Long employeeId){
        Employee employee = employeeService.findById(employeeId);
        Link deleteLink = linkTo(methodOn(EmployeeController.class)
                .deleteById(employee.getId())).withRel("deleteEmployee");
        employee.add(deleteLink);

        Raise raise = raiseServiceProxy.findRaise();
        employee.setSalary(employee.getSalary() * (100 + employee.chooseRaise(raise))/100);
        return ResponseEntity.ok().body(employee);
    }
}
