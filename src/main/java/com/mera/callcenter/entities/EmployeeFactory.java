package com.mera.callcenter.entities;

import org.jboss.logging.Logger;

import java.util.*;

public class EmployeeFactory {

    private static final Logger LOGGER = Logger.getLogger(EmployeeFactory.class);

    public static Employee buildManager() {
        return new Employee(EmployeeType.MANAGER, UUID.randomUUID());
    }

    public static Employee buildSupervisor() {
        return new Employee(EmployeeType.SUPERVISOR, UUID.randomUUID());
    }

    public static Employee buildOperator() {
        return new Employee(EmployeeType.OPERATOR, UUID.randomUUID());
    }

    public static List<Employee> buildRandomEmployees(int maxNumberEmployees){
        LOGGER.debug("Building " + maxNumberEmployees + " employees");
        List<Employee> employees = new ArrayList<>();
        for(int i = 0; i < maxNumberEmployees; i++){
            employees.add(buildRandomEmployee());
        }
        return employees;
    }

    public static List<Employee> buildBasicHierarchyEmployees(){
        return Arrays.asList(
                buildOperator(),
                buildSupervisor(),
                buildOperator(),
                buildManager()
        );
    }

    private static Employee buildRandomEmployee(){
        Random rand = new Random();
        int index = rand.nextInt(3);
        EmployeeType type = EmployeeType.values()[index];
        Employee e ;
        switch (type){
            case MANAGER:
                e = buildManager();
                break;
            case OPERATOR:
                e = buildOperator();
                break;
            case SUPERVISOR:
                e = buildSupervisor();
                break;
            default:
                e = buildOperator();
                break;
        }
        return e;
    }
}
