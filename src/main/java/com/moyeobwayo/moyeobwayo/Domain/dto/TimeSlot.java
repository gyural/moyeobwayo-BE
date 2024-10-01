package com.moyeobwayo.moyeobwayo.Domain.dto;

import java.time.LocalDateTime;


public class TimeSlot {
    String user;
    LocalDateTime start;
    LocalDateTime end;

    public TimeSlot(String user, LocalDateTime start, LocalDateTime end) {
        this.user = user;
        this.start = start;
        this.end = end;
    }

    // Getter and Setter methods
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}

