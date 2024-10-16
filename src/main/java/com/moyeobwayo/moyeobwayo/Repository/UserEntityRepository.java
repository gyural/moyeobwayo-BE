package com.moyeobwayo.moyeobwayo.Repository;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findUserEntitiesByParty(Party party);

    @Query("SELECT u.kakaoProfile.kakaoUserId FROM UserEntity u WHERE u.party.partyId = :partyId")
    Optional<Long> findKakaoIDByPartyId(@Param("partyId") String partyId); // ★ partyId가 String으로 처리됨

    // ★ 파티 ID가 String으로 처리되도록 변경
    @Query("SELECT u FROM UserEntity u WHERE u.user_name = :user_name AND u.party.partyId = :partyId")
    Optional<UserEntity> findUserInSameParty(@Param("user_name") String user_name, @Param("partyId") String partyId);

    // ★ 파티 ID가 String으로 처리되도록 변경
    @Query("SELECT u FROM UserEntity u WHERE u.user_id = :currentUserId AND u.party.partyId = :partyId")
    Optional<UserEntity> findByIdAndPartyId(@Param("currentUserId") int currentUserId, @Param("partyId") String partyId);

    List<UserEntity> findUserEntitiesByKakaoProfile_KakaoUserId(Long kakaoUserId);

    Optional<UserEntity> findByKakaoProfile_KakaoUserId(Long kakaoUserId);
}
