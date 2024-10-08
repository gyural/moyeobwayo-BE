package com.moyeobwayo.moyeobwayo.KakaoUser;

import com.moyeobwayo.moyeobwayo.Domain.KakaoProfile;
import com.moyeobwayo.moyeobwayo.Repository.KakaoProfileRepository;
import com.moyeobwayo.moyeobwayo.Service.KakaoUserService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class KakaoUserServiceTest {

    @Autowired
    private KakaoUserService kakaoTokenService;  // refreshKakaoAccToken 함수를 포함한 서비스

    @Autowired
    private KakaoProfileRepository kakaoProfileRepository; // DB에서 KakaoProfile을 가져올 리포지토리

    @Test
    public void testRefreshKakaoAccToken() {
        // 실제 DB에서 KakaoProfile 가져오기 (테스트를 위한 더미 데이터가 있는지 확인해야 함)
        KakaoProfile kakaoProfile = kakaoProfileRepository.findById(1L).orElseThrow(() ->
                new RuntimeException("No KakaoProfile found for testing"));

        // refreshKakaoAccToken 호출
        ResponseEntity<?> response = kakaoTokenService.refreshKakaoAccToken(kakaoProfile);

        // 결과 확인 (응답이 성공적인지 확인)
        KakaoProfile newKakaoProfile = kakaoProfileRepository.findById(1L).orElseThrow(() ->
                new RuntimeException("No KakaoProfile found for testing"));
        // 결과 확인 (응답이 성공적인지 확인)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        //DB반영 확인
        String ResAccessToken = extractAccessTokenFromResponse(response.getBody().toString());
        assertThat(ResAccessToken).isEqualTo(newKakaoProfile.getAccess_token());
    }
    private String extractAccessTokenFromResponse(String responseBody) {
        // JSON 파싱하여 access_token 추출 (간단한 구현)
        return responseBody.split("\"access_token\":\"")[1].split("\"")[0];
    }
}
