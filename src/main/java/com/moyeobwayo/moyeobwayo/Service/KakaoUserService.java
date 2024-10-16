package com.moyeobwayo.moyeobwayo.Service;
import java.io.BufferedReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.moyeobwayo.moyeobwayo.Domain.Alarm;
import com.moyeobwayo.moyeobwayo.Domain.KakaoProfile;
import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.KakaoProfileRepository;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class KakaoUserService {

    private final KakaoProfileRepository kakaoProfileRepository;
    private final UserEntityRepository userEntityRepository;

    public KakaoUserService(KakaoProfileRepository kakaoProfileRepository, UserEntityRepository userEntityRepository) {
        this.kakaoProfileRepository = kakaoProfileRepository;
        this.userEntityRepository = userEntityRepository;
    }
    @Value("${KAKAO_REST_KEY}")
    private String KAKAO_REST_KEY;

    //UserList중에 카카오 유저만 함수 호출
    public void sendKakaoCompletMesage(List<UserEntity> users, Party party, Date completeDate) {
        for (UserEntity user : users) {
            // 카카오 유저라면 메시지 보내기 (예: 카카오 API 호출)
            if (validateAlarmState(user)) {
                sendCompleteMessage(user.getKakaoProfile(), party, completeDate);
            }
        }

    }
    public boolean validateAlarmState(UserEntity userEntity){
        //전체 알람이 Off라면 False
        if(userEntity.getKakaoProfile() == null || userEntity.getKakaoProfile().isAlarm_off() == true){
            return false;
        }
        //해당 알람만 Off라면 False
        if(userEntity.getAlarm() == null || userEntity.getAlarm().isAlarm_on()==false){
            return false;
        }

        return true;
    }
    // 1. Date 객체를 받아 UTC로 변환하는 함수
    public static String convertToUTC(Date date) {
        // Date를 Instant로 변환
        Instant instant = date.toInstant();
        // UTC에서 ZonedDateTime으로 변환
        ZonedDateTime utcTime = instant.atZone(ZoneId.of("UTC"));

        // 포맷터 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return utcTime.format(formatter)+"Z";
    }

    // 2. Date 객체로부터 1시간 뒤의 endTime을 UTC로 계산하는 함수
    public static String getEndTimeFromStartTime(Date date) {
        // Date를 Instant로 변환
        Instant instant = date.toInstant();
        // 서울 시간대에서 ZonedDateTime으로 변환
        ZonedDateTime utcTime = instant.atZone(ZoneId.of("UTC")).plusHours(1);
        // 1시간 뒤의 종료 시간 계산
        // 포맷터 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return utcTime.format(formatter)+"Z";
    }

    //한 카카오 유저에게 메시지 전송
    public void sendCompleteMessage(KakaoProfile kakaoUser, Party party, Date completeDate) {

        // 1. JSON 템플릿 로드및 기본값 설정
        String startTimeUTC = convertToUTC(completeDate);
        String endTimeUTC = getEndTimeFromStartTime(completeDate);

        JSONObject schedule = new JSONObject();
        schedule.put("title", "모여봐요 " + party.getParty_name());
        // 시간 설정
        JSONObject time = new JSONObject();
        time.put("start_at", startTimeUTC);
        time.put("end_at", endTimeUTC);
        time.put("time_zone", "Asia/Seoul");
        schedule.put("time", time);
        // 설명 설정
        schedule.put("description", party.getParty_description() != null ? party.getParty_description() : "기본 설명입니다.");
        // 위치 설정
        JSONObject location = new JSONObject();
        location.put("name", party.getLocation_name() != null ? party.getLocation_name() : "장소 미정");
        location.put("location_id", 18577297);
        location.put("address", "고려대학교 세종캠퍼스");
        location.put("latitude", 36.610964);
        location.put("longitude", 127.286750);
        schedule.put("location", location);

        // reminders 설정
        List<Integer> testReminders = Arrays.asList(getNearRemindMinute(completeDate), 60);
        JSONArray remindersArray = new JSONArray();
        for (Integer reminder : testReminders) {
            remindersArray.add(reminder);
        }
        schedule.put("reminders", remindersArray); // reminders 추가
        //모여봐요 메인컬러와 가장 유사한 색 선정
        schedule.put("color", "LAVENDER");
        // 3. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Authorization", "Bearer " + kakaoUser.getAccess_token());

        // 4. 요청 바디 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("event", schedule.toJSONString());

        // 5. 요청 엔터티 생성 (헤더와 바디 포함)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // 6. RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();
        // 7. API 호출 및 응답 받기
        String url = "https://kapi.kakao.com/v2/api/calendar/create/event";
        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            int statusCode = response.getStatusCodeValue();
            if (statusCode >= 200 && statusCode < 300) {
                //System.out.println("message send success!!");
            }else {
                //System.out.println("Error!!" + response.getStatusCode() + response.getBody());
            }
        }catch (HttpClientErrorException e) {
            // 클라이언트 오류 (4xx)
            //System.out.println(e.getResponseBodyAsString());
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                //아래 코드에서 DB값을 수정해야되어서 해당 함수가 모두 완료되고 아래 로직을 작동해야함
                ResponseEntity<?> refreshResponse =  refreshKakaoAccToken(kakaoUser);
                if (refreshResponse.getStatusCode().is2xxSuccessful()){
                    String newAccToekn = extractAccessTokenFromResponse(refreshResponse.getBody().toString());
                    kakaoUser.setAccess_token(newAccToekn);
                    sendCompleteMessage(kakaoUser, party, completeDate);
                }else {
                }
            }else if(e.getStatusCode() == HttpStatus.FORBIDDEN){
                //로그아웃 처리 해야함
            }
        } catch (HttpServerErrorException e) {
            // 서버 오류 (5xx)
            //System.out.println(e.getResponseBodyAsString());
        } catch (Exception e) {
            // 그 외의 모든 예외 처리
            //System.out.println(e.getMessage());
        }
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
        try{
            // 5. API 호출
            String url = "https://kauth.kakao.com/oauth/token";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // 6. 응답 결과 처리
            if (response.getStatusCode().is2xxSuccessful()) {

                String newAccToekn = extractAccessTokenFromResponse(response.getBody());
                kakaoProfile.setAccess_token(newAccToekn);
                //DB에 반영
                try{
                    kakaoProfileRepository.save(kakaoProfile);
                }
                catch (Exception e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                }
                kakaoProfileRepository.save(kakaoProfile);

                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        }catch (HttpClientErrorException e) {
            // 클라이언트 오류 (4xx)
        } catch (HttpServerErrorException e) {
            // 서버 오류 (5xx)
        } catch (Exception e) {
            // 그 외의 모든 예외 처리
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
    // refreshing 응답에서 acc토큰 추출함수
    private String extractAccessTokenFromResponse(String responseBody) {
        // JSON 파싱하여 access_token 추출 (간단한 구현)
        return responseBody.split("\"access_token\":\"")[1].split("\"")[0];
    }


    // 🌟 카카오 유저생성 및 조회로직
    public KakaoProfile createUser(String code) {
        // 1. 인가 코드로 액세스 토큰, 리프레시 토큰, 만료 시간 가져오기
        Map<String, Object> tokenInfo = getAccessTokenFromKakao(code);

        // 2. 액세스 토큰으로 사용자 정보 조회
        String accessToken = (String) tokenInfo.get("access_token");
        KakaoProfile kakaoProfile = getKakaoUserProfile(accessToken);

        // 3. 액세스 토큰 및 리프레시 토큰, 만료 시간 설정
        kakaoProfile.setAccess_token(accessToken);
        kakaoProfile.setRefresh_token((String) tokenInfo.get("refresh_token"));
        kakaoProfile.setExpires_in(convertToLong(tokenInfo.get("expires_in")));
        kakaoProfile.setRefresh_token_expires_in(convertToLong(tokenInfo.get("refresh_token_expires_in")));

        // 4. DB에 저장
        return kakaoProfileRepository.save(kakaoProfile);
    }

    // 인가 코드를 통해 액세스 토큰, 리프레시 토큰, 만료 시간 정보 가져오기
    private Map<String, Object> getAccessTokenFromKakao(String code) {
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

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return response.getBody();  // 전체 응답을 반환하여 필요한 값들을 추출
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

        // ★ 여기서 id를 설정할 때, 정확하게 `longValue()`를 사용하여 변환합니다.
        if (body.get("id") instanceof Integer) {
            // 만약 `id` 값이 Integer일 경우 Long으로 명시적으로 변환
            kakaoProfile.setKakaoUserId(((Integer) body.get("id")).longValue());
        } else if (body.get("id") instanceof Long) {
            // 만약 `id` 값이 이미 Long 타입이라면 그대로 사용
            kakaoProfile.setKakaoUserId((Long) body.get("id"));
        } else {
            // 예상치 못한 타입일 경우 예외 처리
            throw new IllegalArgumentException("Unexpected ID type: " + body.get("id").getClass());
        }
        kakaoProfile.setNickname((String) profile.get("nickname"));
        kakaoProfile.setProfile_image((String) profile.get("profile_image_url"));

        return kakaoProfile;
    }

    private Long convertToLong(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue(); // Integer를 Long으로 변환
        } else if (value instanceof Long) {
            return (Long) value; // 이미 Long 타입이면 그대로 반환
        } else {
            throw new IllegalArgumentException("Cannot convert value to Long: " + value);
        }
    }

    // 🌟 새로운 linkUserToKakaoWithKakaoId 메서드
    public boolean linkUserToKakaoWithKakaoId(int currentUserId, String partyId, Long kakaoUserId) {
        // 1. 전달받은 currentUserId와 partyId로 UserEntity 조회
        Optional<UserEntity> userOptional = userEntityRepository.findByIdAndPartyId(currentUserId, partyId);
        if (userOptional.isEmpty()) {
            return false;  // 해당 UserEntity가 존재하지 않으면 연결 불가
        }

        UserEntity userEntity = userOptional.get();

        // 2. DB에서 전달받은 kakao_user_id로 KakaoProfile 조회
        Optional<KakaoProfile> kakaoProfileOptional = kakaoProfileRepository.findById(kakaoUserId);
        if (kakaoProfileOptional.isEmpty()) {
            return false;  // 해당 KakaoProfile이 없으면 연결 불가
        }

        KakaoProfile kakaoProfile = kakaoProfileOptional.get();

        // 3. UserEntity에 KakaoProfile 연결
        userEntity.setKakaoProfile(kakaoProfile);

        // 4. DB에 UserEntity 저장
        userEntityRepository.save(userEntity);

        return true;
    }

    public Integer getNearRemindMinute(Date targetDate) {
        // 현재 시간 가져오기
        Date currentDate = new Date();

        // targetDate와 currentDate의 차이를 밀리초 단위로 계산
        long differenceInMillis = targetDate.getTime() - currentDate.getTime();

        // 차이를 분으로 변환
        long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis);

        // 5의 배수로 내림
        int nearestMultipleOfFive = (int) (Math.floor(differenceInMinutes / 5.0) * 5);
        if (nearestMultipleOfFive > 10) {
            nearestMultipleOfFive = nearestMultipleOfFive - 5;
        } else {
            nearestMultipleOfFive = 10;
        }
        return nearestMultipleOfFive;
    }
    @Transactional
    public boolean updateKakaoUserSettings(Long kakaoUserId, boolean kakaoMessageAllow, boolean alarmOff) {
        Optional<KakaoProfile> optionalProfile = kakaoProfileRepository.findById(kakaoUserId);
        if (optionalProfile.isEmpty()) {
            return false;
        }

        KakaoProfile kakaoProfile = optionalProfile.get();
        kakaoProfile.setKakao_message_allow(kakaoMessageAllow);  // 전달받은 값으로 설정
        kakaoProfile.setAlarm_off(alarmOff);                     // 전달받은 값으로 설정

        kakaoProfileRepository.save(kakaoProfile);  // DB에 저장하여 반영
        return true;
    }
}



