package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import org.springframework.stereotype.Service;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;

import java.util.Optional;


@Service
public class UserService {

    private final UserEntityRepository userRepository;
    private final PartyRepository partyRepository;

    public UserService(UserEntityRepository userRepository, PartyRepository partyRepository) {
        this.userRepository = userRepository;
        this.partyRepository = partyRepository;
    }

    // 로그인 로직: 파티 내 중복 이름 확인 및 로그인 처리
    public Optional<UserEntity> login(String userName, String password, String partyId) {
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

        // 사용자 정보를 반환
        return Optional.of(newUser);
    }
}
