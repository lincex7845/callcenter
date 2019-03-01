package com.mera.callcenter.businesslogic;

import com.mera.callcenter.entities.Call;
import com.mera.callcenter.entities.Employee;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.mera.callcenter.businesslogic.EmployeePredicates.*;

public interface HandleCallStrategy {

    Logger LOGGER = Logger.getLogger(HandleCallStrategy.class);

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

    static void assignIncomingCall(Call call, List<Employee> employees, ConcurrentLinkedQueue<Call> callsOnHold){
        LOGGER.debug("Looking for available employees to assign the call");
        Optional<Employee> availableOperator = findAvailableOperators(employees);
        if(availableOperator.isPresent()){
            LOGGER.debug("Assigning call to an available operator");
            availableOperator.get().assignCall(call);
        }
        else{
            LOGGER.debug("There is not available operators; looking for an available supervisor");
            Optional<Employee> availableSupervisor = findAvailableSupervisors(employees);
            if(availableSupervisor.isPresent()){
                LOGGER.debug("Assigning call to an available supervisor");
                availableSupervisor.get().assignCall(call);
            }
            else{
                LOGGER.debug("There is not available supervisors; looking for an available manager");
                Optional<Employee> availableManager = findAvailableManagers(employees);
                if(availableManager.isPresent()){
                    LOGGER.debug("Assigning call to an available manager");
                    availableManager.get().assignCall(call);
                }
                else{
                    LOGGER.debug("There is not available employees; putting the call " + call + " on-hold");
                    callsOnHold.add(call);
                }
            }
        }
    }

    static void assignOnHoldCalls(List<Employee> employees, ConcurrentLinkedQueue<Call> callsOnHold){
        if(!callsOnHold.isEmpty()){
            LOGGER.debug("Looking for available employees to assign the on-hold call ");
            Call c = callsOnHold.poll();
            assignIncomingCall(c, employees, callsOnHold);
        }
    }
}
