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
//    List<UserEntity> findUserEntitiesByParty(Party party);

    // 1. 특정 파티 내에서 중복된 사용자 이름 확인
    @Query("SELECT u FROM UserEntity u WHERE u.user_name = :userName AND u.party.party_id = :partyId")
    Optional<UserEntity> findUserInSameParty(@Param("userName") String userName, @Param("partyId") int partyId);

    // 2. 사용자 이름과 비밀번호를 기반으로 사용자 조회
    Optional<UserEntity> findByUserNameAndPassword(String user_name, String password);
}
