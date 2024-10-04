package com.moyeobwayo.moyeobwayo.Service;
import java.io.BufferedReader;
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
                System.out.println("Message sent successfully!");
                System.out.println("Response Body: " + response.getBody());
            } else if (statusCode == 401) {
                //권한 요청 로직 설정
                System.out.println("Error: 401 Unauthorized - Access token may be invalid or expired. Attempting to refresh the token.");
            } else if (statusCode == 403) {
                refreshKakaoAccToken(kakaoUser);
                Integer targetID = kakaoUser.getKakao_user_id();
                Optional<KakaoProfile> newKakaoProfile = kakaoProfileRepository.findById(targetID);
                if (newKakaoProfile.isPresent()) {
                    if(kakaoUser.getAccess_token() == newKakaoProfile.get().getAccess_token()){
                        System.out.println("Kakao profile NOT updated!");
                    }else{
                        sendCompleteMessage(newKakaoProfile.get(), party, completeDate);
                    }
                }
                sendCompleteMessage(kakaoUser, party, completeDate);

                System.out.println("Error: 403 Forbidden - Access denied. Please check your permissions or the access token.");
            } else {
                System.out.println("Error: " + statusCode);
                System.out.println("Response Body: " + response.getBody());
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
}