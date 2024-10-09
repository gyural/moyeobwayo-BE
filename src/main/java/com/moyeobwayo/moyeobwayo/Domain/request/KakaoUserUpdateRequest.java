package com.moyeobwayo.moyeobwayo.Domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserUpdateRequest {
    private Long kakao_user_id;          // 카카오 유저 ID
    private boolean kakao_message_allow; // 메시지 수신 허용 여부
    private boolean alarm_off;           // 알림 설정
}