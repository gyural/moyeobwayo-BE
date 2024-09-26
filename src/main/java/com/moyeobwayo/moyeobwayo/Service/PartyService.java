package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCompleteRequest;
import com.moyeobwayo.moyeobwayo.Repository.DateEntityRepsitory;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import com.moyeobwayo.moyeobwayo.Repository.TimeslotRepository;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PartyService {
    private PartyRepository partyRepository;
    private UserEntityRepository userRepository;
    private TimeslotRepository timeslotRepository;
    private DateEntityRepsitory dateEntityRepsitory;
    private KakaoUserService kakaoUserService;
    public PartyService(PartyRepository partyRepository, UserEntityRepository userRepository, TimeslotRepository timeslotRepository,
                        DateEntityRepsitory dateEntityRepsitory, KakaoUserService kakaoUserService) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.timeslotRepository = timeslotRepository;
        this.dateEntityRepsitory = dateEntityRepsitory;
        this.kakaoUserService = kakaoUserService;
    }

    public ResponseEntity<?> partyComplete(int id, PartyCompleteRequest partyCompleteRequest){
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
            System.out.println("요청받은 시간"+reqDate);
            // 확정 시간 DB 반영
            party.setDecision_date(reqDate);
            try {
                List<UserEntity> possibleUsers = getPossibleUsers(party, reqDate);
                // 각 유저의 ID 출력
                for (UserEntity user : possibleUsers) {
                    System.out.println("User ID: " + user.getUser_name());
                }
                // 메시지 전송
                sendCompleteMsg(possibleUsers, party);
                partyRepository.save(party);
                return ResponseEntity.ok(party);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }
        }else{
            return ResponseEntity.badRequest().body("Error: No such user");
        }
    }
    // 필수값 검증 모듈
    public ResponseEntity<?> validateRequireValues(PartyCompleteRequest partyCompleteRequest) {
        if (partyCompleteRequest.getUserId() == null || partyCompleteRequest.getUserId().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: User ID is required.");
        }
        if (partyCompleteRequest.getCompleteTime() == null) {
            return ResponseEntity.badRequest().body("Error: Complete time is required.");
        }
        return null;  // 검증 통과 시 null 반환
    }

    // 파티 존재 검증 모듈
    public ResponseEntity<?> validatePartyExist(int id) {
        Party party = partyRepository.findById(id).orElse(null);
        if (party == null) {
            return ResponseEntity.badRequest().body("Error: Party not found");
        }
        return null;  // 파티가 존재하면 null 반환
    }
    // 파티내의 목표시간에 가능한 유저리스트 반환
    public List<UserEntity> getPossibleUsers( Party party, Date targetDate) {
        // DateID 조회
        Integer targetDateID = dateEntityRepsitory.findDateIdByPartyAndSelectedDate(party.getParty_id(), targetDate);
        // 특정 시간 범위 안에 있는 UserEntity 조회
        return timeslotRepository.findUsersByDateAndTime(targetDateID, targetDate);
    }
    public void sendCompleteMsg (List<UserEntity> targetUsers, Party party){
        // kakao 확정 메시지 보내기
        kakaoUserService.sendKakaoCompletMesage(targetUsers);
    }
}
