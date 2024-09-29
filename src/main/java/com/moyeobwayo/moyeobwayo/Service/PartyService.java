package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.DateEntity;
import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCompleteRequest;
import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCreateRequest;
import com.moyeobwayo.moyeobwayo.Repository.DateEntityRepsitory;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import com.moyeobwayo.moyeobwayo.Repository.TimeslotRepository;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PartyService {
    private PartyRepository partyRepository;
    private UserEntityRepository userRepository;
    private TimeslotRepository timeslotRepository;
    private DateEntityRepsitory dateEntityRepsitory;
    // private KakaoUserService kakaoUserService;

    /**
     * 의존성 주입
     * @param partyRepository
     * @param userRepository
     * @param timeslotRepository
     * @param dateEntityRepsitory
     * @param kakaoUserService
     */
    public PartyService(PartyRepository partyRepository, UserEntityRepository userRepository, TimeslotRepository timeslotRepository,
                        DateEntityRepsitory dateEntityRepsitory){//, KakaoUserService kakaoUserService) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.timeslotRepository = timeslotRepository;
        this.dateEntityRepsitory = dateEntityRepsitory;
        // his.kakaoUserService = kakaoUserService;
    }

//    /**
//     * POST api/v1/party/complete/{id}
//     * 일정 확정
//     * @param id
//     * @param partyCompleteRequest
//     * @return
//     */
//    public ResponseEntity<?> partyComplete(int id, PartyCompleteRequest partyCompleteRequest){
//        // 1. 필수값 검증
//        ResponseEntity<?> validationResponse = validateRequireValues(partyCompleteRequest);
//        if (validationResponse != null) {
//            return validationResponse;
//        }
//
//        // 2. 파티 존재 검증
//        ResponseEntity<?> partyValidationResponse = validatePartyExist(id);
//        if (partyValidationResponse != null) {
//            return partyValidationResponse;
//        }
//
//        Party party = partyRepository.findById(id).orElse(null);
//        List<UserEntity> targetUsers = userRepository.findUserEntitiesByParty(party);
//        if(targetUsers.size() > 0){
//            // kakaoUser라면 리마인드 메시지 전송
//            Date reqDate = partyCompleteRequest.getCompleteTime();
//            System.out.println("요청받은 시간"+reqDate);
//            // 확정 시간 DB 반영
//            party.setDecision_date(reqDate);
//            try {
//                List<UserEntity> possibleUsers = getPossibleUsers(party, reqDate);
//                // 각 유저의 ID 출력
//                for (UserEntity user : possibleUsers) {
//                    System.out.println("User ID: " + user.getUser_name());
//                }
//                // 메시지 전송
//                sendCompleteMsg(possibleUsers, party, reqDate);
//                partyRepository.save(party);
//                return ResponseEntity.ok(party);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//            }
//        }else{
//            return ResponseEntity.badRequest().body("Error: No such user");
//        }
//    }
//
//    // 필수값 검증 모듈
//    public ResponseEntity<?> validateRequireValues(PartyCompleteRequest partyCompleteRequest) {
//        if (partyCompleteRequest.getUserId() == null || partyCompleteRequest.getUserId().isEmpty()) {
//            return ResponseEntity.badRequest().body("Error: User ID is required.");
//        }
//        if (partyCompleteRequest.getCompleteTime() == null) {
//            return ResponseEntity.badRequest().body("Error: Complete time is required.");
//        }
//        return null;  // 검증 통과 시 null 반환
//    }
//
//    // 파티 존재 검증 모듈
//    public ResponseEntity<?> validatePartyExist(int id) {
//        Party party = partyRepository.findById(id).orElse(null);
//        if (party == null) {
//            return ResponseEntity.badRequest().body("Error: Party not found");
//        }
//        return null;  // 파티가 존재하면 null 반환
//    }
//    // 파티내의 목표시간에 가능한 유저리스트 반환
//    public List<UserEntity> getPossibleUsers( Party party, Date targetDate) {
//        // DateID 조회
//        Integer targetDateID = dateEntityRepsitory.findDateIdByPartyAndSelectedDate(party.getParty_id(), targetDate);
//        if (targetDateID == null) {
//            System.out.println("targetDateID is null");
//            return new ArrayList<UserEntity>();  // 빈 배열 반환
//        }
//        // 특정 시간 범위 안에 있는 UserEntity 조회
//        return timeslotRepository.findUsersByDateAndTime(targetDateID, targetDate);
//    }
//    public void sendCompleteMsg (List<UserEntity> targetUsers, Party party, Date completeDate){
//        // kakao 확정 메시지 보내기
//        kakaoUserService.sendKakaoCompletMesage(targetUsers, party, completeDate);
//    }


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
            party.setEnd_date(partyCreateRequest.getEndTime());
            party.setDecision_date(partyCreateRequest.getDecisionDate());
            party= partyRepository.save(party); // db에 저장 후 저장된 객체 반환(자동 생성된 id를 가져오기 위해)

            // 방금 생성한 Party 테이블 튜플의 pk 가져오기
            int party_id = party.getParty_id();

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
}
