package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.KakaoProfile;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.KakaoProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KakaoUserService {
    private final KakaoProfileRepository kakaoProfileRepository;
    public KakaoUserService(KakaoProfileRepository kakaoProfileRepository) {
        this.kakaoProfileRepository = kakaoProfileRepository;
    }
    public void sendKakaoCompletMesage(List<UserEntity> users) {
        for (UserEntity user : users) {
            // 사용자별 메시지 생성 (예: 사용자 이름 포함 메시지)
            String message = "안녕하세요, " + user.getUser_name() + "님. 일정이 확정되었습니다.";

            // 카카오 메시지를 보내는 로직 (예: 카카오 API 호출)
            if(user.getKakaoProfile() != null){
                System.out.println(message);
                sendKakaoMessage(user.getKakaoProfile(), message);
            }
        }
    }

    private void sendKakaoMessage(KakaoProfile kakaoID, String message) {
        // 실제 카카오 API를 호출하는 메서드
        // 카카오 메시지 전송 로직 구현
        System.out.println("Sending message to Kakao ID: " + kakaoID.getNickname() + " - " + message);
    }

}
