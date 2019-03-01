package com.mera.callcenter.entities;

import java.util.Random;
import java.util.UUID;

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

    public static Call buildRandomCall(){
        Random rand = new Random();
        long duration = (long)rand.nextInt(6) + MIN_DURATION;
        return new Call(duration);
    }

    @Override
    public String toString(){
        return String.format("Call [id: %s, duration: %s sec]", this.id, this.duration);
    }
}
