// 안씀
package com.moyeobwayo.moyeobwayo.Domain.dto;

import java.util.Date;

public class TimeSlotDTO {
    private int user_id;  // user_id 필드로 수정
    private Date start;
    private Date end;

    public TimeSlotDTO(int user_id, Date start, Date end) {
        this.user_id = user_id;
        this.start = start;
        this.end = end;
    }

    // Getter and Setter methods
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}