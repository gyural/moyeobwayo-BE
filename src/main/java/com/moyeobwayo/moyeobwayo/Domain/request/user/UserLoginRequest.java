package com.moyeobwayo.moyeobwayo.Domain.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    private String userName;
    private String password;
}