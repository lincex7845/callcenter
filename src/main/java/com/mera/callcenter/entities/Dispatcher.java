package com.mera.callcenter.entities;

import com.mera.callcenter.businesslogic.HandleCallStrategy;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Dispatcher implements HandleCallStrategy, Runnable {

    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class);

    private List<Employee> employees;
    private ConcurrentLinkedQueue<Call> callsOnHold;

    public Dispatcher(List<Employee> employees){
        this.employees = employees;
        this.callsOnHold = new ConcurrentLinkedQueue<>();
    }

    public synchronized void dispatchCall(Call call){
        HandleCallStrategy.assignIncomingCall(call, employees, callsOnHold);
    }

    @Override
    public void run() {
        while(true){
            HandleCallStrategy.assignOnHoldCalls(employees, callsOnHold);
        }
    }
}
