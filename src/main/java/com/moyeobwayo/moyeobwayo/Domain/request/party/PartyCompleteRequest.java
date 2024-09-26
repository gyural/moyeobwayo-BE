package com.moyeobwayo.moyeobwayo.Domain.request.party;

import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class PartyCompleteRequest {
    private String userId;
    private Date completeTime;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
