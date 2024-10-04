package com.moyeobwayo.moyeobwayo.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.TimeZone;

@Entity
@Getter
@Setter
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int slot_id;

    private Date selected_start_time;
    private Date selected_end_time;

    @ManyToOne
    @JoinColumn(name = "date_id")
    @JsonIgnore  // 순환 참조 방지
    private DateEntity date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore  // 순환 참조 방지
    private UserEntity userEntity;

    // 유저 ID 반환
    @Transient
    public int getUserId() {
        return userEntity != null ? userEntity.getUser_id() : 0;
    }

    // 파티 ID 반환
    @Transient
    public int getPartyId() {
        return date != null && date.getParty() != null ? date.getParty().getParty_id() : 0;
    }

    // 날짜 ID 반환
    @Transient
    public int getDateId() {
        return date != null ? date.getDate_id() : 0;
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
