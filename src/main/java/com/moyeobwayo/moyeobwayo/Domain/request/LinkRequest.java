package com.moyeobwayo.moyeobwayo.Domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkRequest {
    private Long userId;
    private int partyId;
    private int kakaoUserId;
}