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
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> { // Integer로 변경

    List<UserEntity> findUserEntitiesByParty(Party party);

    // 특정 이름과 Party ID로 UserEntity 찾기
    @Query("SELECT u FROM UserEntity u WHERE u.user_name = :userName AND u.party.party_id = :partyId")
    Optional<UserEntity> findUserInSameParty(@Param("userName") String userName, @Param("partyId") int partyId);

    // 특정 ID와 Party ID로 UserEntity 찾기
    @Query("SELECT u FROM UserEntity u WHERE u.user_id = :userId AND u.party.party_id = :partyId")
    Optional<UserEntity> findByIdAndPartyId(@Param("userId") int userId, @Param("partyId") int partyId); // userId를 int로 유지
}
