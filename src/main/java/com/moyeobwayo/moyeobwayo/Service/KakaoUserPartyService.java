package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KakaoUserPartyService {

    private final UserEntityRepository userEntityRepository;

    @Autowired
    public KakaoUserPartyService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    public List<Party> getPartyByKakaoUserId(int kakaoUserId) {
        // kakao_user_id로 UserEntity 조회
        List<Party> userEntity = userEntityRepository.findByUserEntity_KakaoProfile_KakaoUserId(kakaoUserId);


        if (userEntity.isEmpty()) {
            return null;
        }
        return userEntity;

    }
}
