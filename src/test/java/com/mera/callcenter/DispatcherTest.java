package com.mera.callcenter;

import com.mera.callcenter.entities.Call;
import com.mera.callcenter.entities.Employee;
import com.mera.callcenter.entities.EmployeeStatus;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.mera.callcenter.Dispatcher.MAX_NUMBER_CALLS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DispatcherTest {

    private static final Logger LOGGER = Logger.getLogger(DispatcherTest.class);

    private List<Employee> employeeList;
    private List<Call> callList;
    private ConcurrentLinkedQueue<Call> waitingCalls;


    @Before
    public void before(){
        employeeList = CallCenterFactory.buildBasicHierarchyEmployees();
        LOGGER.info(employeeList.size() + " employees created");
        callList = CallCenterFactory.buildRandomCalls(MAX_NUMBER_CALLS);
        LOGGER.info(callList.size() + " calls created");
        waitingCalls = new ConcurrentLinkedQueue<>();
    }

    private static List<Callable<Boolean>> createTasks(List<Call> calls,
                                                       List<Employee> employees,
                                                       ConcurrentLinkedQueue<Call> waitingCalls){
        final List<Callable<Boolean>> tasks = new ArrayList<>();
        calls.forEach(c -> {
            Callable<Boolean> t = () -> {
                LOGGER.info("Invoking dispatchCall from " + Thread.currentThread().getName());
                Dispatcher.dispatchCall(c, employees, waitingCalls);
                return true;
            };
            tasks.add(t);
        });
        return tasks;
    }

    @Test
    public void test10ConcurrentCalls(){
        LOGGER.info("starting test10ConcurrentCalls");
        final List<Callable<Boolean>> tasks = createTasks(callList, employeeList, waitingCalls);
        final ExecutorService exServ = Executors.newFixedThreadPool(MAX_NUMBER_CALLS);
        try {
            LOGGER.info("Invoking 10 concurrent calls");
            List<Future<Boolean>> futures = exServ.invokeAll(tasks);
            for(Future<Boolean> f : futures)
                f.get();
            LOGGER.info("Checking if all the employees have assigned at least one call");
            for(Employee e: employeeList){
                assertTrue(!e.getPendingCalls().isEmpty());
            }
            LOGGER.info("Checking if there is at least one waiting call");
            assertTrue(!waitingCalls.isEmpty());
            assertTrue(waitingCalls.size() > 4);
        }
        catch (InterruptedException | ExecutionException e){
            fail("test10ConcurrentCalls failed");
        }
        finally {
            exServ.shutdown();
        }
    }

    @Test
    public void test10ConcurrentCallsWithBusyEmployees(){
        LOGGER.info("starting test10ConcurrentCallsWithBusyEmployees");
        List<Employee> busyEmployees = new ArrayList<>(employeeList);
        busyEmployees.forEach(e -> e.setEmployeestatus(EmployeeStatus.BUSY));
        final List<Callable<Boolean>> tasks = createTasks(callList, busyEmployees, waitingCalls);
        final ExecutorService exServ2 = Executors.newFixedThreadPool(MAX_NUMBER_CALLS);
        try {
            LOGGER.info("Invoking 10 concurrent calls");
            List<Future<Boolean>> futures = exServ2.invokeAll(tasks);
            for(Future<Boolean> f : futures)
                f.get();
            LOGGER.info("Checking if none of the employees have assigned at least one call");
            for(Employee e: employeeList){
                assertTrue(e.getPendingCalls().isEmpty());
            }
            LOGGER.info("Checking if there are 10 waiting calls");
            assertTrue(!waitingCalls.isEmpty());
            assertEquals(10, waitingCalls.size());
        }
        catch (InterruptedException | ExecutionException e){
            fail("test10ConcurrentCallsWithBusyEmployees failed");
        }
        finally {
            exServ2.shutdown();
        }
    }

    @Test
    public void testMoreThan10ConcurrentCalls(){
        LOGGER.info("starting testMoreThan10ConcurrentCalls");
        final List<Call> calls = new ArrayList<>(callList);
        calls.add(CallCenterFactory.buildRandomCall());
        final List<Callable<Boolean>> tasks = createTasks(calls, employeeList, waitingCalls);
        final ExecutorService exServ3 = Executors.newFixedThreadPool(MAX_NUMBER_CALLS);
        try {
            LOGGER.info("Invoking 11 concurrent calls");
            List<Future<Boolean>> futures = exServ3.invokeAll(tasks);
            for(Future<Boolean> f : futures)
                f.get();
            LOGGER.info("Checking if all the employees have at least one call assigned");
            for(Employee e: employeeList){
                assertTrue(!e.getPendingCalls().isEmpty());
            }
            LOGGER.info("Checking if there is at least one waiting call");
            assertTrue(!waitingCalls.isEmpty());
            assertTrue(waitingCalls.size() > 5);
        }
        catch (InterruptedException | ExecutionException e){
            fail("testMoreThan10ConcurrentCalls failed");
        }
        finally {
            exServ3.shutdown();
        }
    }

    @Test
    public void testMoreThan10ConcurrentCallsWithBusyEmployees(){
        LOGGER.info("starting testMoreThan10ConcurrentCallsWithBusyEmployees");
        List<Employee> busyEmployees = new ArrayList<>(employeeList);
        busyEmployees.forEach(e -> e.setEmployeestatus(EmployeeStatus.BUSY));
        final List<Call> calls = new ArrayList<>(callList);
        calls.add(CallCenterFactory.buildRandomCall());
        calls.add(CallCenterFactory.buildRandomCall());
        calls.add(CallCenterFactory.buildRandomCall());
        final List<Callable<Boolean>> tasks = createTasks(calls, busyEmployees, waitingCalls);
        final ExecutorService exServ4 = Executors.newFixedThreadPool(MAX_NUMBER_CALLS);
        try {
            LOGGER.info("Invoking 13 concurrent calls");
            List<Future<Boolean>> futures = exServ4.invokeAll(tasks);
            for(Future<Boolean> f : futures)
                f.get();
            LOGGER.info("Checking if none of the employees have at least one call assigned");
            for(Employee e: employeeList){
                assertTrue(e.getPendingCalls().isEmpty());
            }
            LOGGER.info("Checking if there are 13 waiting calls");
            assertTrue(!waitingCalls.isEmpty());
            assertEquals(13, waitingCalls.size());
        }
        catch (InterruptedException | ExecutionException e){
            fail("testMoreThan10ConcurrentCallsWithBusyEmployees failed");
        }
        finally {
            exServ4.shutdown();
        }
    }
}
