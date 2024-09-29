package com.moyeobwayo.moyeobwayo.Controller;

import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCompleteRequest;
import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCreateRequest;
import com.moyeobwayo.moyeobwayo.Service.PartyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public ResponseEntity<?> createParty(@RequestBody PartyCreateRequest partyCreateRequest) {
        return partyService.partyCreate(partyCreateRequest);
    }
}
