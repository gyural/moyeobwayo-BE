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
    private int kakaoUserId;

    private String profileImage;
    private String nickname;

    private String access_token;
    private String refresh_token;

    private boolean kakaoMessageAllow;
    private boolean alarm_off;
}
