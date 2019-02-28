package com.mera.callcenter.entities;

import com.mera.callcenter.domain.HandleCallStrategy;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Dispatcher implements HandleCallStrategy {

    private List<Employee> employees;
    private ConcurrentLinkedQueue<Call> callsOnHold;

    public Dispatcher(List<Employee> employees){
        this.employees = employees;
        this.callsOnHold = new ConcurrentLinkedQueue<>();
    }

    public synchronized void dispatchCall(Call call){
        HandleCallStrategy.handleIncomingCall(call, employees, callsOnHold);
    }
}
