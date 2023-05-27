package com.example.employees.service;

import com.example.employees.exception.EmailAlreadyExistsException;
import com.example.employees.exception.EmployeeNotFoundException;
import com.example.employees.model.Employee;
import com.example.employees.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAll(){
        return employeeRepository.findAll();
    }

    public Employee findByEmail(String email){
        return employeeRepository.findEmployeeByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with email " + email + " not found"));
    }

    public Employee add(Employee employee){
        Optional<Employee> optionalEmployee = employeeRepository.findEmployeeByEmail(employee.getEmail());
        if (optionalEmployee.isPresent()){
            throw new EmailAlreadyExistsException("Employee with email " + employee.getEmail() + " already exists!");
        }
        return employeeRepository.save(employee);
    }

    public Employee findById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
    }

    public Employee deleteById(Long id){
        Employee employee = findById(id);
        employeeRepository.deleteById(id);
        return employee;
    }
}
