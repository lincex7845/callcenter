package com.mera.callcenter.entities;

/**
 * A set of statuses to determine if an employee could answer a call
 */
public enum EmployeeStatus {
    AVAILABLE,
    ON_CALL,    // the employee has been assigned for a call but he/she haven't answered it yet
    BUSY        // the employee is answering a call
}
