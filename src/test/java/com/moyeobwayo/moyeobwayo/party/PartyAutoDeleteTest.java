package com.moyeobwayo.moyeobwayo.party;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import com.moyeobwayo.moyeobwayo.Service.PartyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PartyAutoDeleteTest {

    @Autowired
    private PartyService partyService;

    @Autowired
    private PartyRepository partyRepository;

    @Test
    @Transactional
    public void 만료된파티자동삭제테스트() {
        // Step 1: 이미 지난 날짜의 endDate를 가진 파티 생성
        Party expiredParty = new Party();
        expiredParty.setParty_name("Expired Party");
        expiredParty.setParty_description("This party is expired.");
        expiredParty.setTarget_num(10);
        expiredParty.setCurrent_num(5);
        expiredParty.setStart_date(new Date(System.currentTimeMillis() - 86400000)); // 현재 시간으로부터 1일 전
        expiredParty.setEndDate(new Date(System.currentTimeMillis() - 3600000)); // 현재 시간으로부터 1시간 전
        partyRepository.save(expiredParty);

        // Step 2: 미래의 endDate를 가진 파티 생성
        Party futureParty = new Party();
        futureParty.setParty_name("Future Party");
        futureParty.setParty_description("This party is still valid.");
        futureParty.setTarget_num(10);
        futureParty.setCurrent_num(5);
        futureParty.setStart_date(new Date());
        futureParty.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // 현재 시간으로부터 1일 후
        partyRepository.save(futureParty);

        // Step 3: 자동 삭제 메서드 실행
        partyService.deleteExpiredParties();

        // Step 4: 만료된 파티가 삭제되었는지 검증
        List<Party> remainingParties = partyRepository.findAll();
        assertThat(remainingParties).doesNotContain(expiredParty);
        assertThat(remainingParties).contains(futureParty);
    }
}
