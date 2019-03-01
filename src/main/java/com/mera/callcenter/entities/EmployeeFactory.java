package com.mera.callcenter.entities;

import org.jboss.logging.Logger;

import java.util.*;

public class EmployeeFactory {

    private static final Logger LOGGER = Logger.getLogger(EmployeeFactory.class);

    public static Employee buildManager() {
        //LOGGER.info("Building manager");
        return new Employee(EmployeeType.MANAGER, UUID.randomUUID());
    }

    public static Employee buildSupervisor() {
        //LOGGER.info("Building supervisor");
        return new Employee(EmployeeType.SUPERVISOR, UUID.randomUUID());
    }

    public static Employee buildOperator() {
        //LOGGER.info("Building operator");
        return new Employee(EmployeeType.OPERATOR, UUID.randomUUID());
    }

    public static List<Employee> buildRandomEmployees(int maxNumberEmployees){
        //LOGGER.info("Building " + maxNumberEmployees + " employees");
        List<Employee> employees = new ArrayList<>();
        for(int i = 0; i < maxNumberEmployees; i++){
            employees.add(buildRandomEmployee());
        }
        return employees;
    }

    public static List<Employee> buildBasicHierarchyEmployees(){
        List<Employee> employees = Arrays.asList(
                buildOperator(),
                buildSupervisor(),
                buildOperator(),
                buildManager()
        );

        return employees;
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
