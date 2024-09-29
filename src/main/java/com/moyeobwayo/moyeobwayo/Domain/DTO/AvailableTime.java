package com.moyeobwayo.moyeobwayo.Domain.DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AvailableTime {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> users;

    public AvailableTime(LocalDateTime start, LocalDateTime end, List<String> users) {
        this.start = start;
        this.end = end;
        this.users = users;
    }

    // Getter and Setter methods
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

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "[" + start.format(formatter) + ", " + end.format(formatter) + ", " + users + "]";
    }
}
