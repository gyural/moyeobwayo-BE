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

    @ManyToOne
    @JoinColumn(name = "kakao_user_id")
    private KakaoProfile kakaoProfile; // 해당 table의 기본키를 참조

    private String user_name;
    private String password;

    @OneToMany(mappedBy = "userEntity")
    private List<Alarm> alarms;

    //양방향 관계 필요 없고 무한루프를 유발함
    //@OneToMany(mappedBy = "userEntity")
    //@JsonIgnore // Timeslot에서 UserEntity를 참조할 때 무시
    //private List<Timeslot> timeslots;

    @ManyToOne
    @JoinColumn(name="party_id")
    private Party party;

}
