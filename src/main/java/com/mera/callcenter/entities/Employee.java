package com.mera.callcenter.entities;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

public class Employee implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Employee.class);

    private UUID id;
    private EmployeeStatus status;
    private EmployeeType type;
    private ConcurrentLinkedQueue<Call> pendingCalls;
    private ConcurrentLinkedDeque<Call> answeredCalls;

    public Employee(EmployeeType type, UUID id) {
        this.id = id;
        this.type = type;
        this.status = EmployeeStatus.AVAILABLE;
        this.pendingCalls = new ConcurrentLinkedQueue<>();
        this.answeredCalls = new ConcurrentLinkedDeque<>();
    }

    public synchronized EmployeeStatus getStatus() {
        return status;
    }

    public synchronized void setEmployeestatus(EmployeeStatus s){ this.status = s; }

    public synchronized EmployeeType getType() {
        return type;
    }

    public synchronized ConcurrentLinkedQueue<Call> getPendingCalls() {
        return pendingCalls;
    }

    public synchronized void assignCall(Call c) {
        if(status.equals(EmployeeStatus.AVAILABLE)){
            this.pendingCalls.add(c);
            this.setEmployeestatus(EmployeeStatus.ON_CALL);
            LOGGER.debug("Assigning call " + c + " to " + this.type + " - " + this.id);
        }
    }

    public ConcurrentLinkedDeque<Call> getAnsweredCalls() {
        return answeredCalls;
    }

    @Override
    public void run() {
        while (true) {
            LOGGER.info(String.format("%s is waiting for calls", toString()));
            Optional<Call> maybeCall = Optional.ofNullable(this.pendingCalls.peek());
            if (maybeCall.isPresent() && this.getStatus().equals(EmployeeStatus.ON_CALL)) {
                Call c = maybeCall.get();
                this.setEmployeestatus(EmployeeStatus.BUSY);
                try {
                    TimeUnit.SECONDS.sleep(c.getDuration());
                } catch (InterruptedException e) {
                    LOGGER.error(String.format("An error occurred during the call attended by %s %s", this.type, this.id), e);
                } finally {
                    LOGGER.debug(String.format("The call %s last %d s", c.getId(), c.getDuration()));
                    this.pendingCalls.poll();
                    this.answeredCalls.add(c);
                    this.setEmployeestatus(EmployeeStatus.AVAILABLE);
                }
            }
        }
    }

    @Override
    public String toString(){
        return String.format("Employee [ID: %s, Type: %s, Status: %s, Answered: %s, Assigned: %s])", id, type, status, answeredCalls.size(), pendingCalls.size());
    }
}
