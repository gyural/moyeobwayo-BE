package com.moyeobwayo.moyeobwayo.Domain.dto;

import java.util.Date;

public class TimeslotRequestDTO {

    private Date selected_start_time;
    private Date selected_end_time;
    private int user_id;
    private int date_id;

    // Getters and Setters
    public Date getSelected_start_time() {
        return selected_start_time;
    }

    public void setSelected_start_time(Date selected_start_time) {
        this.selected_start_time = selected_start_time;
    }

    public Date getSelected_end_time() {
        return selected_end_time;
    }

    public void setSelected_end_time(Date selected_end_time) {
        this.selected_end_time = selected_end_time;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getDate_id() {
        return date_id;
    }

    public void setDate_id(int date_id) {
        this.date_id = date_id;
    }
}
