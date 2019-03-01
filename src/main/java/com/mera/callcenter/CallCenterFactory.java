package com.mera.callcenter;

import com.mera.callcenter.entities.Call;
import com.mera.callcenter.entities.Employee;
import com.mera.callcenter.entities.EmployeeType;
import org.jboss.logging.Logger;

import java.util.*;

import static com.mera.callcenter.entities.Call.MIN_DURATION;

/**
 * A factory pattern implementation to create objects on demand
 */
public class CallCenterFactory {

    private static final Logger LOGGER = Logger.getLogger(CallCenterFactory.class);

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

    public static Employee buildRandomEmployee(){
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

    public static Call buildRandomCall(){
        Random rand = new Random();
        long duration = (long)rand.nextInt(6) + MIN_DURATION;
        return new Call(duration);
    }

    public static List<Call> buildRandomCalls(int numberOfCalls){
        List<Call> calls = new ArrayList<>();
        for(int i = 0; i < numberOfCalls; i++){
            calls.add(buildRandomCall());
        }
        return calls;
    }
}
