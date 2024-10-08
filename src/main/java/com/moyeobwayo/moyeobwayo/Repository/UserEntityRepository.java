package com.moyeobwayo.moyeobwayo.Repository;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findUserEntitiesByParty(Party party);

    Optional<UserEntity> findByKakaoProfile_KakaoUserId(int kakaoUserId); // Optional로 변경

    @Query("SELECT u.party FROM UserEntity u WHERE u.kakaoProfile.kakaoUserId = :kakaoUserId")
    List<Party> findUserEntitiesPartiesByKakaoProfile_KakaoUserId(@Param("kakaoUserId") int kakaoUserId);    List<UserEntity> findUserEntitiesByKakaoProfile_KakaoUserId(int kakaoUserId);
//파티 리스트를 KakaoProfile_KakaoUserId 를 통해 가져오고싶어

}
