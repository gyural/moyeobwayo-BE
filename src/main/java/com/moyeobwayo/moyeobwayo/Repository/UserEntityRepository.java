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

    // 특정 이름과 Party ID로 UserEntity 찾기
    // 수정된 쿼리 메서드: @Param 어노테이션의 변수명을 정확히 맞춤
    @Query("SELECT u FROM UserEntity u WHERE u.user_name = :user_name AND u.party.party_id = :party_id")
    Optional<UserEntity> findUserInSameParty(@Param("user_name") String user_name, @Param("party_id") String party_id);

    // 특정 ID와 Party ID로 UserEntity 찾기
    @Query("SELECT u FROM UserEntity u WHERE u.user_id = :currentUserId AND u.party.party_id = :partyId")
    Optional<UserEntity> findByIdAndPartyId(@Param("currentUserId") int currentUserId, @Param("partyId") String partyId);


    //Optional<UserEntity> findByKakaoProfile_KakaoUserId(Long kakaoUserId); // Optional로 변경


//    @Query("SELECT u.party FROM UserEntity u WHERE u.kakaoProfile.kakao_user_id = :kakaoUserId")
  //  List<Party> findUserEntitiesPartiesByKakaoProfile_KakaoUserId(@Param("kakaoUserId") Long kakaoUserId);
    List<UserEntity> findUserEntitiesByKakaoProfile_KakaoUserId(Long kakaoUserId);
//파티 리스트를 KakaoProfile_KakaoUserId 를 통해 가져오고싶어

    }
