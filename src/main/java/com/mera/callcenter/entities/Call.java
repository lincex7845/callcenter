package com.mera.callcenter.entities;

import java.util.UUID;

/**
 * It represents the call
 */
public class Call {

    private UUID id;
    private long duration;
    public static long MIN_DURATION = 5;

    public Call(long duration) {
        this.duration = duration;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString(){
        return String.format("Call [id: %s, duration: %s sec]", this.id, this.duration);
    }
}
