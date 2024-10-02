package com.moyeobwayo.moyeobwayo.Service;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import com.moyeobwayo.moyeobwayo.Domain.KakaoProfile;
import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.KakaoProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.ZoneId;

@Service
public class KakaoUserService {

    private final KakaoProfileRepository kakaoProfileRepository;

    public KakaoUserService(KakaoProfileRepository kakaoProfileRepository) {
        this.kakaoProfileRepository = kakaoProfileRepository;
    }
    @Value("${KAKAO_REST_KEY}")
    private String KAKAO_REST_KEY;

    //UserList중에 카카오 유저만 함수 호출
    public void sendKakaoCompletMesage(List<UserEntity> users, Party party, Date completeDate) {
        for (UserEntity user : users) {
            // 카카오 유저라면 메시지 보내기 (예: 카카오 API 호출)
            if (user.getKakaoProfile() != null) {
                sendCompleteMessage(user.getKakaoProfile(), party, completeDate);
            }
        }
    }
    //한 카카오 유저에게 메시지 전송
    public void sendCompleteMessage(KakaoProfile kakaoUser, Party party, Date completeDate) {
        try {
            // 1. JSON 템플릿 로드
            String template = loadJsonTemplate("src/main/resources/static/message_template.json");

            // 2. JSON 템플릿에서 값을 동적으로 대체
            String message = template.replace("{{party_title}}", party.getParty_name())
                    .replace("{{complete_time}}", formatDate(completeDate))
                    .replace("{{location}}", "미정");

            // 3. 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            headers.set("Authorization", "Bearer " + kakaoUser.getAccess_token());

            // 4. 요청 바디 설정
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("template_object", message);

            // 5. 요청 엔터티 생성 (헤더와 바디 포함)
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            // 6. RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();

            // 7. API 호출 및 응답 받기
            String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // 8. 응답 처리
            int statusCode = response.getStatusCodeValue();
            if (statusCode >= 200 && statusCode < 300) {
            } else if (statusCode == 401) {
                //권한 요청 로직 설정
            } else if (statusCode == 403) {
                refreshKakaoAccToken(kakaoUser);
                Integer targetID = kakaoUser.getKakao_user_id();
                Optional<KakaoProfile> newKakaoProfile = kakaoProfileRepository.findById(targetID);
                if (newKakaoProfile.isPresent()) {
                    if(kakaoUser.getAccess_token() == newKakaoProfile.get().getAccess_token()){
                    }else{
                        sendCompleteMessage(newKakaoProfile.get(), party, completeDate);
                    }
                }
                sendCompleteMessage(kakaoUser, party, completeDate);

            } else {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // JSON 템플릿을 로드하는 함수
    private String loadJsonTemplate(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String formatDate(Date completeDate) {
        // Date -> LocalDateTime 변환
        LocalDateTime localDateTime = completeDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        // "MM월 dd일 HH시 mm분" 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분");
        return localDateTime.format(formatter);
    }

    public ResponseEntity<?> refreshKakaoAccToken(KakaoProfile kakaoProfile) {
        // 1. RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        // 3. 요청 바디 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", KAKAO_REST_KEY); // 앱 REST API 키
        body.add("refresh_token", kakaoProfile.getRefresh_token());

        // 4. 요청 엔터티 생성 (헤더와 바디를 포함)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // 5. API 호출
        String url = "https://kauth.kakao.com/oauth/token";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // 6. 응답 결과 처리
        if (response.getStatusCode().is2xxSuccessful()) {

            String newAccToekn = extractAccessTokenFromResponse(response.getBody());
            kakaoProfile.setAccess_token(newAccToekn);
            //DB에 반영
            try{
                kakaoProfileRepository.save(kakaoProfile)                ;
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            kakaoProfileRepository.save(kakaoProfile);

            return ResponseEntity.ok(response.getBody());
        } else {
            System.out.println("Error: " + response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }
    // refreshing 응답에서 acc토큰 추출함수
    private String extractAccessTokenFromResponse(String responseBody) {
        // JSON 파싱하여 access_token 추출 (간단한 구현)
        return responseBody.split("\"access_token\":\"")[1].split("\"")[0];
    }


    // 🌟 카카오 유저생성 및 조회로직

    public KakaoProfile createUser(String code) {
        // 1. 인가 코드로 액세스 토큰 가져오기
        String accessToken = getAccessTokenFromKakao(code);

        // 2. 액세스 토큰으로 사용자 정보 조회
        KakaoProfile kakaoProfile = getKakaoUserProfile(accessToken);

        // 3. DB에 저장
        return kakaoProfileRepository.save(kakaoProfile);
    }

    // 인가 코드를 통해 액세스 토큰 발급 로직 추가
    private String getAccessTokenFromKakao(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_REST_KEY); // 카카오 REST API 키
        params.add("redirect_uri", "http://127.0.0.1:3000/login/oauth/callback/kakao"); // 설정된 리다이렉트 URI
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // 액세스 토큰 추출
        return extractAccessTokenFromResponse(response.getBody());
    }

    // 액세스 토큰으로 카카오 사용자 프로필 정보 조회
    private KakaoProfile getKakaoUserProfile(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> body = response.getBody();

        // 사용자 정보 추출 및 KakaoProfile 객체 생성
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        KakaoProfile kakaoProfile = new KakaoProfile();
        kakaoProfile.setKakao_user_id((int) body.get("id")); // 카카오 사용자 ID 설정
        kakaoProfile.setNickname((String) profile.get("nickname"));
        kakaoProfile.setProfile_image((String) profile.get("profile_image_url"));
        kakaoProfile.setAccess_token(accessToken);

        return kakaoProfile;
    }
}



