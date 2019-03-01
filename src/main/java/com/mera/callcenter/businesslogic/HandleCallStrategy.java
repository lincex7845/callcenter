package com.mera.callcenter.businesslogic;

import com.mera.callcenter.entities.Call;
import com.mera.callcenter.entities.Employee;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.mera.callcenter.businesslogic.EmployeePredicates.*;

/**
 * An implementation of strategy pattern to handle incoming and waiting calls
 */
public interface HandleCallStrategy {

    Logger LOGGER = Logger.getLogger(HandleCallStrategy.class);

    /**
     * A method to find an employee of {@link com.mera.callcenter.entities.EmployeeType#OPERATOR }
     * who is {@link com.mera.callcenter.entities.EmployeeStatus#AVAILABLE }
     * @param employees the list of all employees
     * @return Optional value which represents whether or not exists an available operator
     */
    static Optional<Employee> findAvailableOperators(List<Employee> employees){
        return employees
                .stream()
                .filter(isOperator())
                .filter(isAvailable())
                .findFirst();
    }

    /**
     * A method to find an employee of {@link com.mera.callcenter.entities.EmployeeType#SUPERVISOR }
     * who is {@link com.mera.callcenter.entities.EmployeeStatus#AVAILABLE }
     * @param employees the list of all employees
     * @return Optional value which represents whether or not exists an available supervisor
     */
    static Optional<Employee> findAvailableSupervisors(List<Employee> employees){
        return employees
                .stream()
                .filter(isSupervisor())
                .filter(isAvailable())
                .findFirst();
    }

    /**
     * A method to find an employee of {@link com.mera.callcenter.entities.EmployeeType#MANAGER }
     * who is {@link com.mera.callcenter.entities.EmployeeStatus#AVAILABLE }
     * @param employees the list of all employees
     * @return Optional value which represents whether or not exists an available manager
     */
    static Optional<Employee> findAvailableManagers(List<Employee> employees){
        return employees
                .stream()
                .filter(isManager())
                .filter(isAvailable())
                .findFirst();
    }

    /**
     * A method to determine how to assign an incoming call to the first available employee.
     * In case of none of the employees is available the call is holden (on-hold)
     * @param call Incomming call
     * @param employees List of all employees
     * @param callsOnHold thread-safe queue to store waiting calls temporally
     */
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

    /**
     * A method to asign waiting calls to the first available employee.
     * Under the hood implements {@link #assignIncomingCall(Call, List, ConcurrentLinkedQueue)}
     * @param employees list of all employees
     * @param callsOnHold thread-safe queue to pick up the first waiting call
     */
    static void assignOnHoldCalls(List<Employee> employees, ConcurrentLinkedQueue<Call> callsOnHold){
        if(!callsOnHold.isEmpty()){
            LOGGER.debug("Looking for available employees to assign the waiting call ");
            Call c = callsOnHold.poll();
            assignIncomingCall(c, employees, callsOnHold);
        }
    }
}
