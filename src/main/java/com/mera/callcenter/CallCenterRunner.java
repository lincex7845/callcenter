package com.mera.callcenter;

import com.mera.callcenter.entities.Call;
import com.mera.callcenter.entities.Dispatcher;
import com.mera.callcenter.entities.Employee;
import com.mera.callcenter.entities.EmployeeFactory;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CallCenterRunner {

    public static final int MAX_NUMBER_CALLS = 10;
    private static final Logger LOGGER = Logger.getLogger(CallCenterRunner.class);

    public static void main(String[] args) {
        final List<Employee> employees = EmployeeFactory.buildBasicHierarchyEmployees();
        final ExecutorService callsExecutorService = Executors.newFixedThreadPool(10);
        final ExecutorService employeesExecutorService = Executors.newFixedThreadPool(employees.size());
        final ExecutorService dispatcherExecutorService = Executors.newSingleThreadExecutor();
        final Dispatcher dispatcher = new Dispatcher(employees);

        try {
            final List<Callable<Boolean>> tasks = new ArrayList<>();

            for (int i = 0; i < MAX_NUMBER_CALLS; i++) {
                Callable<Boolean> task = () -> {
                    Call c = Call.buildRandomCall();
                    //LOGGER.info("call created " + c);
                    dispatcher.dispatchCall(c);
                    return true;
                };
                tasks.add(task);
            }

            dispatcherExecutorService.execute(dispatcher);

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
            LOGGER.info("finally...");
            dispatcherExecutorService.shutdown();
            employeesExecutorService.shutdown();
            callsExecutorService.shutdown();
        }
    }
}
