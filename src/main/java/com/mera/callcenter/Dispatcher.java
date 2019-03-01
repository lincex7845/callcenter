package com.mera.callcenter;

import com.mera.callcenter.businesslogic.HandleCallStrategy;
import com.mera.callcenter.entities.Call;
import com.mera.callcenter.entities.Employee;
import com.mera.callcenter.entities.EmployeeFactory;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class Dispatcher implements HandleCallStrategy {

    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class);
    private static final int MAX_NUMBER_CALLS = 10;

    private synchronized static void dispatchCall(Call call,List<Employee> employees, ConcurrentLinkedQueue<Call> callsOnHold){
        HandleCallStrategy.assignIncomingCall(call, employees, callsOnHold);
    }

    public static void main(String[] args) {

        final List<Employee> employees = EmployeeFactory.buildBasicHierarchyEmployees();
        final ConcurrentLinkedQueue<Call> callsOnHold = new ConcurrentLinkedQueue<>();

        final ExecutorService callsExecutorService = Executors.newFixedThreadPool(MAX_NUMBER_CALLS);
        final ExecutorService employeesExecutorService = Executors.newFixedThreadPool(employees.size());
        final ExecutorService dispatcherExecutorService = Executors.newSingleThreadExecutor();

        Runnable handleWaitingCalls = () -> {
            while(true){
                HandleCallStrategy.assignOnHoldCalls(employees, callsOnHold);
            }
        };

        try {
            final List<Callable<Boolean>> tasks = new ArrayList<>();

            for (int i = 0; i < MAX_NUMBER_CALLS; i++) {
                Callable<Boolean> task = () -> {
                    Call c = Call.buildRandomCall();
                    LOGGER.debug("Incoming " + c);
                    dispatchCall(c, employees, callsOnHold);
                    return true;
                };
                tasks.add(task);
            }

            dispatcherExecutorService.execute(handleWaitingCalls);

            List<Future<Boolean>> futures = callsExecutorService.invokeAll(tasks);

            List<Future<Object>> futureEmployees =  employeesExecutorService.invokeAll(employees
                    .stream()
                    .map(Executors::callable)
                    .collect(Collectors.toList()));


            for(Future<Boolean> f : futures)
                LOGGER.info("Future of call returned: " + f.get());

            for(Future<Object> f : futureEmployees)
                LOGGER.info("Future of employee returned: " + f.get());

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("An error occurred dispatching the calls", e);
        }
        finally {
            dispatcherExecutorService.shutdown();
            employeesExecutorService.shutdown();
            callsExecutorService.shutdown();
        }
    }
}
