package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import org.springframework.stereotype.Service;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;

import java.util.Optional;


@Service
public class UserService {

    private final UserEntityRepository userRepository;

    public UserService(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 로그인 로직: 파티 내 중복 이름 확인 및 로그인 처리
    public Optional<UserEntity> login(String userName, String password, int partyId) {
        // 1. 해당 파티에 중복된 이름 확인
        Optional<UserEntity> existingUser = userRepository.findUserInSameParty(userName, partyId);
        if (existingUser.isPresent()) {
            return Optional.empty();  // 중복된 이름이 있는 경우 로그인 실패
        }
        UserEntity newUser = new UserEntity();
        newUser.setUser_name(userName);
        newUser.setPassword(password);
        newUser = userRepository.save(newUser);
        // 2. 사용자 이름과 비밀번호로 로그인
        return Optional.of(newUser);
    }
}
