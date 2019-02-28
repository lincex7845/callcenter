package com.mera.callcenter;

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

    private Employee(EmployeeType type, UUID id) {
        this.id = id;
        this.type = type;
        this.status = EmployeeStatus.AVAILABLE;
        this.pendingCalls = new ConcurrentLinkedQueue<>();
        this.answeredCalls = new ConcurrentLinkedDeque<>();
    }

    public static Employee buildManager() {
        return new Employee(EmployeeType.MANAGER, UUID.randomUUID());
    }

    public static Employee buildSupervisor() {
        return new Employee(EmployeeType.SUPERVISOR, UUID.randomUUID());
    }

    public static Employee buildOperator() {
        return new Employee(EmployeeType.OPERATOR, UUID.randomUUID());
    }

    public synchronized EmployeeStatus getStatus() {
        return status;
    }

    public EmployeeType getType() {
        return type;
    }

    public synchronized ConcurrentLinkedQueue<Call> getPendingCalls() {
        return pendingCalls;
    }

    public synchronized void assignCall(Call c) {
        this.pendingCalls.add(c);
    }

    public ConcurrentLinkedDeque<Call> getAnsweredCalls() {
        return answeredCalls;
    }

    @Override
    public void run() {
        while (true) {
            LOGGER.info(String.format("%s %s is polling for calls", this.type, this.id));
            Optional<Call> maybeCall = Optional.of(this.pendingCalls.poll());
            if (maybeCall.isPresent()) {
                LOGGER.info(String.format("%s %s is answering a call", this.type, this.id));
                Call c = maybeCall.get();
                this.status = EmployeeStatus.BUSY;
                try {
                    TimeUnit.SECONDS.sleep(c.getDuration());
                } catch (InterruptedException e) {
                    LOGGER.error(String.format("An error occurred during the call attended by %s %s", this.type, this.id), e);
                } finally {
                    this.status = EmployeeStatus.AVAILABLE;
                }
                this.answeredCalls.add(c);
            }
        }
    }
}
