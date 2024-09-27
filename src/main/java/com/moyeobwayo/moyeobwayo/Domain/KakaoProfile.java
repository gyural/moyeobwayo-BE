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

    private String access_token;
    private String refresh_token;

    private boolean kakao_message_allow;
    private boolean alarm_off;
}
