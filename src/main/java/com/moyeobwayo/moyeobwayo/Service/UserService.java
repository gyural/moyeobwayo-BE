package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.Alarm;
import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.AlarmRepository;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;

import java.util.Optional;


@Service
@Transactional
public class UserService {

    private final UserEntityRepository userRepository;
    private final PartyRepository partyRepository;
    private final AlarmRepository alarmRepository;  // 알람 리포지토리 추가

    public UserService(UserEntityRepository userRepository, PartyRepository partyRepository, AlarmRepository alarmRepository) {
        this.userRepository = userRepository;
        this.partyRepository = partyRepository;
        this.alarmRepository = alarmRepository;
    }

    // 로그인 로직: 파티 내 중복 이름 확인 및 로그인 처리

    public Optional<UserEntity> login(String userName, String password, String partyId, boolean isKakao) {
        // 파티 ID로 해당 파티 조회
        Optional<Party> partyOptional = partyRepository.findById(partyId);
        if (partyOptional.isEmpty()) {
            return Optional.empty();  // 해당 파티가 존재하지 않음
        }
        Party party = partyOptional.get();  // 파티가 존재하면 파티 객체를 가져옴

        // 해당 파티에 중복된 이름 확인
        Optional<UserEntity> existingUser = userRepository.findUserInSameParty(userName, partyId);
        if (existingUser.isPresent()) {
            return Optional.empty();  // 중복된 이름이 있는 경우 로그인 실패
        }

        // 새로운 사용자 생성 및 파티 연관 관계 설정
        UserEntity newUser = new UserEntity();
        newUser.setUser_name(userName);
        newUser.setPassword(password);
        newUser.setParty(party);  // 파티와의 관계 설정

        // 새로운 사용자 저장
        newUser = userRepository.save(newUser);

        // 🌟 만약 isKakao가 true라면 알람 테이블에 새로운 알람 추가
        if (isKakao) {
            System.out.println("Creating new Alarm object...");
            Alarm newAlarm = new Alarm();
            newAlarm.setUserEntity(newUser);
            newAlarm.setParty(party);
            newAlarm.setAlarm_on(true);

            // 저장 전 알람 객체 정보 확인
            System.out.println("Alarm Details: User ID: " + newAlarm.getUserEntity().getUser_id() + ", Party ID: " + newAlarm.getParty().getParty_id());

            // 저장 시도
            alarmRepository.save(newAlarm);
            System.out.println("Alarm saved successfully!");
        }

        // 사용자 정보를 반환
        return Optional.of(newUser);
    }
}
