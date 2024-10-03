package com.moyeobwayo.moyeobwayo.party;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PartyCreateTest {

    @Autowired
    private PartyRepository partyRepository;

    @Test
    @Transactional
    public void 파티생성test() {
        // 파티 세팅
        Party party = new Party();
        party.setParty_name("Spring Boot Party");
        party.setParty_description("This is a test party for Spring Boot.");
        party.setTarget_num(10);
        party.setCurrent_num(0); // 현재 인원은 0으로 초기화
        party.setStart_date(new Date());
        party.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // 1일 후

        // 파티 저장
        Party savedParty = partyRepository.save(party);

        // 결과 검증
        assertThat(savedParty).isNotNull();
        assertThat(savedParty.getParty_name()).isEqualTo("Spring Boot Party");
        assertThat(savedParty.getParty_description()).isEqualTo("This is a test party for Spring Boot.");
        assertThat(savedParty.getTarget_num()).isEqualTo(10);
        assertThat(savedParty.getCurrent_num()).isEqualTo(0);
        assertThat(savedParty.getStart_date()).isNotNull();
        assertThat(savedParty.getEndDate()).isNotNull();
        // 날짜 검증: 현재 시간보다 미래의 날짜인지
        assertThat(savedParty.getEndDate()).isAfter(savedParty.getStart_date());
    }
}
