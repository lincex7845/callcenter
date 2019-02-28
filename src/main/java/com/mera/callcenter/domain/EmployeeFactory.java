package com.mera.callcenter.domain;

import com.mera.callcenter.entities.Employee;
import com.mera.callcenter.entities.EmployeeType;

import java.util.UUID;

public class EmployeeFactory {

    public static Employee buildManager() {
        return new Employee(EmployeeType.MANAGER, UUID.randomUUID());
    }

    public static Employee buildSupervisor() {
        return new Employee(EmployeeType.SUPERVISOR, UUID.randomUUID());
    }

    public static Employee buildOperator() {
        return new Employee(EmployeeType.OPERATOR, UUID.randomUUID());
    }

}
