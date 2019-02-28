package com.mera.callcenter.domain;

import com.mera.callcenter.entities.Employee;
import com.mera.callcenter.entities.EmployeeStatus;
import com.mera.callcenter.entities.EmployeeType;

import java.util.function.Predicate;

public interface EmployeePredicates {

    static Predicate<Employee> isAvailable(){
        return e -> e.getStatus().equals(EmployeeStatus.AVAILABLE);
    }

    static Predicate<Employee> isManager(){
        return e -> e.getType().equals(EmployeeType.MANAGER);
    }

    static Predicate<Employee> isSupervisor(){
        return e -> e.getType().equals(EmployeeType.SUPERVISOR);
    }

    static Predicate<Employee> isOperator(){
        return e -> e.getType().equals(EmployeeType.OPERATOR);
    }
}
