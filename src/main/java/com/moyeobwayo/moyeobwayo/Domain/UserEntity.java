package com.moyeobwayo.moyeobwayo.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @OneToOne
    @JoinColumn(name = "kakao_user_id")
    private KakaoProfile kakaoProfile; // 해당 table의 기본키를 참조

    private String user_name;
    private String password;

    @OneToMany(mappedBy = "userEntity")
    private List<Alarm> alarms;

    @OneToMany(mappedBy = "userEntity")
    private List<Timeslot> timeslots;
}
