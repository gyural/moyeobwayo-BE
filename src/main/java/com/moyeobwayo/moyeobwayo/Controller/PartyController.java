package com.moyeobwayo.moyeobwayo.Controller;

import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCreateRequest;
import com.moyeobwayo.moyeobwayo.Service.PartyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.moyeobwayo.moyeobwayo.Domain.DTO.AvailableTime;

import java.util.List;

@RestController
@RequestMapping("api/v1/party")
public class PartyController {

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

//    @PostMapping("/complete/{id}")
//    public ResponseEntity<?> completeParty(@PathVariable int id, @RequestBody PartyCompleteRequest partyCompleteRequest) {
//        return partyService.partyComplete(id, partyCompleteRequest);
//    }

    /**
     * 파티 생성
     * POST api/v1/party/create
     * @param partyCreateRequest
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<?> createParty(@RequestBody PartyCreateRequest partyCreateRequest) {
        return partyService.partyCreate(partyCreateRequest);
    }

    /**
     * 지정된 파티의 가능 여부 높은 시간 출력
     * GET api/v1/party/{id}
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getParty(@PathVariable int id) {
        List<AvailableTime> availableTimes = partyService.findAvailableTimesForParty(id);
        return ResponseEntity.ok(availableTimes);
    }

}
