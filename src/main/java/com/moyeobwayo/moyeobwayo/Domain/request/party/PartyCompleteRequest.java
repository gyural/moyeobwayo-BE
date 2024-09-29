/**
 * 목적: controller가 json으로 받은 body를 하나의 객체로 묶어 처리하기 위해 다음과 같은 클래스 생성
 */
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
@Service // ?? 해당 부분에 왜 Service가 필요한가요??
@AllArgsConstructor
@NoArgsConstructor
public class PartyCompleteRequest {
    private String userId;
    private Date completeTime;

    // 시간대 설정은 현 위치가 아닌 애플리케이션 전역 설정으로 하는 것이 바람직하므로, application.properties로 옮기는 것을 고려해볼 것(이거 왜 있나 물어보니 gpt가 이렇게 말해줬습니다.)
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
