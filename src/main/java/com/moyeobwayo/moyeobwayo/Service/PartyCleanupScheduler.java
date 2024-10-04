package com.moyeobwayo.moyeobwayo.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PartyCleanupScheduler {

    private final PartyService partyService;

    public PartyCleanupScheduler(PartyService partyService) {
        this.partyService = partyService;
    }


    // @Scheduled(cron = "0 * * * * ?") // 1분마다 실행
    // @Scheduled(cron = "*/10 * * * * ?")  // 10초마다 실행

    // 오류나서 일단 주석처리
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void cleanUpExpiredParties() {
//        partyService.deleteExpiredParties();
//    }
}
