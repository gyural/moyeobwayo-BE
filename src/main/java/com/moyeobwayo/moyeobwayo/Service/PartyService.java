package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.*;
import com.moyeobwayo.moyeobwayo.Domain.dto.TimeSlot;
import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCompleteRequest;
import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCreateRequest;
import com.moyeobwayo.moyeobwayo.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.moyeobwayo.moyeobwayo.Domain.dto.AvailableTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.*;
import java.util.Map;

@Service
public class PartyService {
    private final PartyStringIdRepository partyStringIdRepository;
    private PartyRepository partyRepository;
    private UserEntityRepository userRepository;
    private TimeslotRepository timeslotRepository;
    private DateEntityRepsitory dateEntityRepsitory;
    private KakaoUserService kakaoUserService;
    private AlarmRepository alarmRepository;

    // 의존성 주입
    public PartyService(PartyRepository partyRepository,
                        UserEntityRepository userRepository,
                        TimeslotRepository timeslotRepository,
                        DateEntityRepsitory dateEntityRepsitory,
                        KakaoUserService kakaoUserService,
                        PartyStringIdRepository partyStringIdRepository,
                        AlarmRepository alarmRepository) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.timeslotRepository = timeslotRepository;
        this.dateEntityRepsitory = dateEntityRepsitory;
        this.kakaoUserService = kakaoUserService;
        this.partyStringIdRepository = partyStringIdRepository;
        this.alarmRepository = alarmRepository;
    }

    public void updateAlarmStatus(String partyId, String alarmStatus) {
        // partyId로 Alarm 객체 조회
        Optional<Alarm> alarmOptional = alarmRepository.findAlarmByParty_PartyId(partyId);

        if (alarmOptional.isPresent()) {
            Alarm alarm = alarmOptional.get();

            // "on" 또는 "off" 값에 따라 알람 상태를 설정
            if ("on".equalsIgnoreCase(alarmStatus)) {
                alarm.setAlarm_on(true);
            } else if ("off".equalsIgnoreCase(alarmStatus)) {
                alarm.setAlarm_on(false);
            } else {
                throw new IllegalArgumentException("Invalid alarm status value: " + alarmStatus);
            }

            // 변경 사항 저장
            alarmRepository.save(alarm);
        } else {
            throw new EntityNotFoundException("Alarm for Party with ID " + partyId + " not found.");
        }
    }

    /**
     * POST api/v1/party/complete/{id}
     * 일정 확정
     * @param id
     * @param partyCompleteRequest
     * @return
     */
    public ResponseEntity<?> partyComplete(String id, PartyCompleteRequest partyCompleteRequest){
        // 1. 필수값 검증
        ResponseEntity<?> validationResponse = validateRequireValues(partyCompleteRequest);
        if (validationResponse != null) {
            return validationResponse;
        }

        // 2. 파티 존재 검증
        ResponseEntity<?> partyValidationResponse = validatePartyExist(id);
        if (partyValidationResponse != null) {
            return partyValidationResponse;
        }

        Party party = partyRepository.findById(id).orElse(null);
        List<UserEntity> targetUsers = userRepository.findUserEntitiesByParty(party);
        if(targetUsers.size() > 0){
            // kakaoUser라면 리마인드 메시지 전송
            Date reqDate = partyCompleteRequest.getCompleteTime();
            String locationName = partyCompleteRequest.getLocationName() != null ? partyCompleteRequest.getLocationName() : "미정";
            // 확정 시간 DB 반영
            party.setDecision_date(reqDate);
            party.setLocation_name(locationName);
            try {
                List<UserEntity> possibleUsers = getPossibleUsers(party, reqDate);
                // 메시지 전송
                sendCompleteMsg(possibleUsers, party, reqDate);
                partyRepository.save(party);
                return ResponseEntity.ok(party);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }
        }else{
            // 확정 시간 DB 반영
            Date reqDate = partyCompleteRequest.getCompleteTime();
            party.setDecision_date(reqDate);
            return ResponseEntity.ok(party);
        }
    }

    // 필수값 검증 모듈
    public ResponseEntity<?> validateRequireValues(PartyCompleteRequest partyCompleteRequest) {
        if (partyCompleteRequest.getUserId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: User ID is required."));
        }
        if (partyCompleteRequest.getCompleteTime() == null) {
            return ResponseEntity.badRequest().body(Map.of("message",  "Complete time is required."));
        }
        return null;  // 검증 통과 시 null 반환
    }

    // 파티 존재 검증 모듈
    public ResponseEntity<?> validatePartyExist(String id) {
        Party party = partyRepository.findById(id).orElse(null);
        if (party == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Party not found"));
        }
        return null;  // 파티가 존재하면 null 반환
    }
    // 파티내의 목표시간에 가능한 유저리스트 반환
    public List<UserEntity> getPossibleUsers(Party party, Date targetDate) {
        // DateID 조회
        Integer targetDateID = dateEntityRepsitory.findDateIdByPartyAndSelectedDate(party.getPartyId(), targetDate);  // 이제 String으로 처리
        if (targetDateID == null) {
            System.out.println("targetDateID is null");
            return new ArrayList<>();  // 빈 배열 반환
        }
        // 특정 시간 범위 안에 있는 UserEntity 조회
        return timeslotRepository.findUsersByDateAndTime(targetDateID, targetDate);
    }
    public void sendCompleteMsg (List<UserEntity> targetUsers, Party party, Date completeDate){
        // kakao 확정 메시지 보내기
        kakaoUserService.sendKakaoCompletMesage(targetUsers, party, completeDate);
    }


    /**
     * POST api/v1/party/create
     * 파티 생성
     * @param partyCreateRequest
     * @return
     */
    public ResponseEntity<?> partyCreate(PartyCreateRequest partyCreateRequest){
        try{
            // 필수 값 검증(값이 정상적으로 전달되었는지 검증), partyDescription은 null 혹은 empty로 와도 가능하게 함.
            if(partyCreateRequest.getParticipants()<=0 ||
                    partyCreateRequest.getPartyTitle()==null || partyCreateRequest.getPartyTitle().isEmpty() ||
                    partyCreateRequest.getStartTime()==null || partyCreateRequest.getEndTime()==null ||
                    partyCreateRequest.getDates()==null || partyCreateRequest.getDates().isEmpty()){

                return ResponseEntity.badRequest().body("Error: 필요한 값이 전부 넘어오지 않음");
            }

            // 파티 시간 유효성 검증(startTime, endTime이 타당한지)
            if(!partyCreateRequest.getStartTime().before(partyCreateRequest.getEndTime())){
                return ResponseEntity.badRequest().body("Error: 시작 시간보다 종료 시간이 더 빠름");
            }

            // 파티 객체 생성 및 Party 테이블에 삽입
            Party party = new Party();
            party.setTarget_num(partyCreateRequest.getParticipants());
            party.setParty_name(partyCreateRequest.getPartyTitle());
            party.setParty_description(partyCreateRequest.getPartyDescription());
            party.setStart_date(partyCreateRequest.getStartTime());
            party.setEndDate(partyCreateRequest.getEndTime());
            party.setDecision_date(partyCreateRequest.getDecisionDate());
            party.setUser_id(partyCreateRequest.getUserId());
            party= partyRepository.save(party); // db에 저장 후 저장된 객체 반환(자동 생성된 id를 가져오기 위해)

            // 방금 생성한 Party 테이블 튜플의 pk 가져오기
            String party_id = party.getPartyId();

            // Party의 pk와 List<Date>를 이용하여 date_entity 테이블에 삽입
            List<DateEntity> dateEntities = new ArrayList<>();
            for(Date date: partyCreateRequest.getDates()){
                DateEntity dateEntity = new DateEntity();
                dateEntity.setSelected_date(date);
                dateEntity.setParty(party);

                dateEntities.add(dateEntity);
            }
            dateEntityRepsitory.saveAll(dateEntities); // db에 저장

            // 모든 과정이 정상적으로 수행되었다면, status(200) 반환
            return ResponseEntity.ok(party);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    public Party findPartyById(String partyId) {
        return partyStringIdRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("Party not found"));
    }


    /**
     * 특정 파티의 가용 여부 높은 시간을 찾는 메서드
     * @param partyId
     * @return List<AvailableTime>
     */
    public List<AvailableTime> findAvailableTimesForParty(String partyId) {  // !!!!!!!!!!! 수정
        // 1. Party 객체 찾기
        Party party = partyStringIdRepository.findById(partyId)  // !!!!!!!!!!! 수정
                .orElseThrow(() -> new IllegalArgumentException("Party not found"));

        // 2. Party와 연결된 모든 DateEntity의 Timeslot 가져오기
        List<TimeSlot> timeSlots = new ArrayList<>();
        for (DateEntity date : party.getDates()) {
            List<Timeslot> timeslots = timeslotRepository.findAllByDate(date);
            for (Timeslot slot : timeslots) {
                LocalDateTime start = slot.getSelected_start_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = slot.getSelected_end_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                timeSlots.add(new TimeSlot(slot.getUserEntity().getUser_name(), start, end));
            }
        }

        // 3. 가능한 시간대 찾기
        return findAvailableTimes(timeSlots);
    }


    /**
     * 주어진 TimeSlot 리스트를 이용하여 가능한 시간을 찾는 메서드
     * @param timeSlots
     * @return List<AvailableTime>
     */
    public List<AvailableTime> findAvailableTimes(List<TimeSlot> timeSlots) {
        // 모든 시작 및 종료 시간 추출
        Set<LocalDateTime> timePoints = new HashSet<>();
        for (TimeSlot slot : timeSlots) {
            timePoints.add(slot.getStart());
            timePoints.add(slot.getEnd());
        }

        // 시간 순서대로 정렬
        List<LocalDateTime> sortedTimePoints = new ArrayList<>(timePoints);
        Collections.sort(sortedTimePoints);

        // 가능한 모든 시간 범위 생성
        List<AvailableTime> availableTimes = new ArrayList<>();
        for (int i = 0; i < sortedTimePoints.size() - 1; i++) {
            LocalDateTime start = sortedTimePoints.get(i);
            LocalDateTime end = sortedTimePoints.get(i + 1);
            List<String> usersAvailable = new ArrayList<>();

            // 각 사용자에 대해 해당 시간 범위에 가능한지 확인
            for (TimeSlot slot : timeSlots) {
                if (!slot.getStart().isAfter(start) && !slot.getEnd().isBefore(end)) {
                    usersAvailable.add(slot.getUser());
                }
            }

            if (!usersAvailable.isEmpty()) {
                availableTimes.add(new AvailableTime(start, end, usersAvailable));
            }
        }

        // 가능한 사용자 수에 따라 정렬
        availableTimes.sort((a, b) -> b.getUsers().size() - a.getUsers().size());

        return availableTimes;
    }

    /**
     * 만료된 파티를 삭제하는 메서드(url을 통해 접근하지 않기에 컨트롤러 없음)
     */
//    public void deleteExpiredParties() {
//        LocalDateTime currentDateTime = LocalDateTime.now(); // 현재 시간을 LocalDateTime으로 가져오기
//        Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant()); // LocalDateTime을 Date로 변환
//
//        System.out.println("현재 시간: " + currentDate);
//        List<Party> expiredParties = partyRepository.findByEndDateBefore(currentDate);
//
//        if (!expiredParties.isEmpty()) {
//            partyRepository.deleteAll(expiredParties);
//            System.out.println(expiredParties.size() + "개의 만료된 파티를 삭제했습니다.");
//        } else {
//            System.out.println("삭제할 만료된 파티가 없습니다.");
//
//            // 모든 파티의 end_date를 출력
//            List<Party> allParties = partyRepository.findAll();
//            for (Party party : allParties) {
//                System.out.println("Party ID: " + party.getParty_id() + ", End Date: " + party.getEndDate());
//            }
//        }
//    }

    /**
     * 파티 정보 수정
     * @param partyId
     * @param partyUpdateRequest
     * @return
     */
    public ResponseEntity<?> updateParty(String partyId, PartyCreateRequest partyUpdateRequest) {
        try {
            // 1. 파티 존재 여부 확인
            Party existingParty = partyStringIdRepository.findById(partyId) // String 타입 partyId를 처리
                    .orElseThrow(() -> new IllegalArgumentException("Error: Party not found"));


            // 2. 수정할 필드 업데이트
            existingParty.setTarget_num(partyUpdateRequest.getParticipants());
            existingParty.setParty_name(partyUpdateRequest.getPartyTitle());
            existingParty.setParty_description(partyUpdateRequest.getPartyDescription());
            existingParty.setStart_date(partyUpdateRequest.getStartTime());
            existingParty.setEndDate(partyUpdateRequest.getEndTime());
            existingParty.setDecision_date(partyUpdateRequest.getDecisionDate());
            existingParty.setUser_id(partyUpdateRequest.getUserId());

            // 3. DateEntity 업데이트 (기존 리스트를 제거 후 새로 추가)
            List<DateEntity> existingDates = existingParty.getDates();
            dateEntityRepsitory.deleteAll(existingDates); // 기존 날짜 리스트 삭제

            List<DateEntity> newDates = new ArrayList<>();
            for (Date date : partyUpdateRequest.getDates()) {
                DateEntity dateEntity = new DateEntity();
                dateEntity.setSelected_date(date);
                dateEntity.setParty(existingParty);
                newDates.add(dateEntity);
            }
            dateEntityRepsitory.saveAll(newDates); // 새로운 날짜 리스트 저장

            // 4. 파티 정보 저장 (업데이트)
            partyRepository.save(existingParty);

            // 5. 수정된 파티 정보 반환
            return ResponseEntity.ok(existingParty);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    public void disconnectUserFromParty(String partyId) {
        // 파티 ID로 KakaoUserId 조회
        Optional<Long> kakaoUserIdOptional = userRepository.findKakaoIDByPartyId(partyId);

        if (kakaoUserIdOptional.isPresent()) {
            Long kakaoUserId = kakaoUserIdOptional.get();

            // KakaoUserId로 UserEntity 조회
            Optional<UserEntity> userOptional = userRepository.findByKakaoProfile_KakaoUserId(kakaoUserId);

            if (userOptional.isPresent()) {
                UserEntity userEntity = userOptional.get();
                // 파티와의 연결을 해제
                userEntity.setParty(null);
                // 카카오 프로필과의 연결을 해제 (필요하다면)
                userEntity.setKakaoProfile(null);
                // 업데이트된 정보를 저장
                userRepository.save(userEntity);
            } else {
                throw new EntityNotFoundException("User with kakaoUserId " + kakaoUserId + " not found.");
            }
        } else {
            throw new EntityNotFoundException("User with partyId " + partyId + " not found.");
        }
    }

}
