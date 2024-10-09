package com.moyeobwayo.moyeobwayo.Domain.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    // DTO
    private String userName;
    private String password;
    private String partyId;  // 로그인 시 파티 ID도 함께 전달
}