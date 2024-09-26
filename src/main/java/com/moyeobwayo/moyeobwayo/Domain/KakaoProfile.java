package com.moyeobwayo.moyeobwayo.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class KakaoProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int kakao_user_id;

    private String profile_image;
    private String nickname;
    //acctoken을 저장할 예정
    private String kakao_id;
    private boolean alarm_off;

    @OneToOne(mappedBy = "kakaoProfile")
    private UserEntity userEntity;
}
