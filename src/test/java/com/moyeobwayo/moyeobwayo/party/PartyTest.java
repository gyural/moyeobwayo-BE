package com.moyeobwayo.moyeobwayo.party;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@SpringBootTest
public class PartyTest {

    @Autowired
    private PartyRepository partyRepository;

    @Test
    @Transactional
    public void 파티확정test(){
        //파티 세팅
        Party party = new Party();
        party.setParty_name("Spring Boot Party");
        party.setParty_description("This is a test party for Spring Boot.");
        party.setTarget_num(10);
        party.setCurrent_num(5);
        party.setStart_date(new Date());
        party.setEndDate(new Date());

        partyRepository.save(party);
        //User 2명 세팅
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        user1.setUser_name("spring1");
        user2.setUser_name("spring2");


    }
}
