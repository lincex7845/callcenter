package com.mera.callcenter.domain;

import com.mera.callcenter.entities.Call;
import com.mera.callcenter.entities.Employee;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.mera.callcenter.domain.EmployeePredicates.*;

public interface HandleCallStrategy {

    static Optional<Employee> findAvailableOperators(List<Employee> employees){
        return employees
                .stream()
                .filter(isOperator())
                .filter(isAvailable())
                .findFirst();
    }

    static Optional<Employee> findAvailableSupervisors(List<Employee> employees){
        return employees
                .stream()
                .filter(isSupervisor())
                .filter(isAvailable())
                .findFirst();
    }

    static Optional<Employee> findAvailableManagers(List<Employee> employees){
        return employees
                .stream()
                .filter(isManager())
                .filter(isAvailable())
                .findFirst();
    }

    static void handleIncomingCall(Call call, List<Employee> employees, ConcurrentLinkedQueue<Call> callsOnHold){
        Optional<Employee> availableOperator = findAvailableOperators(employees);
        if(availableOperator.isPresent()){
            availableOperator.get().assignCall(call);
        }
        else{
            Optional<Employee> availableSupervisor = findAvailableSupervisors(employees);
            if(availableSupervisor.isPresent()){
                availableSupervisor.get().assignCall(call);
            }
            else{
                Optional<Employee> availableManager = findAvailableManagers(employees);
                if(availableManager.isPresent()){
                    availableManager.get().assignCall(call);
                }
                else{
                    callsOnHold.add(call);
                }
            }
        }
    }

    static void handleHoldenCall(List<Employee> employees, ConcurrentLinkedQueue<Call> callsOnHold){
        if(!callsOnHold.isEmpty()){
            Call c = callsOnHold.poll();
            handleIncomingCall(c, employees, callsOnHold);
        }
    }
}
