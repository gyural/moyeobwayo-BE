package com.moyeobwayo.moyeobwayo.Repository;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findUserEntitiesByParty(Party party);

    Optional<UserEntity> findByKakaoProfile_KakaoUserId(int kakaoUserId); // Optional로 변경

    List<Party> findPartiesByKakaoProfile_KakaoUserId(int kakaoUserId);
    List<Party> findByUserEntity_KakaoProfile_KakaoUserId(int kakaoUserId);

}
