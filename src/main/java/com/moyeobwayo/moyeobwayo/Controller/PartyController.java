package com.moyeobwayo.moyeobwayo.Controller;

import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCompleteRequest;
import com.moyeobwayo.moyeobwayo.Service.PartyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")  // 기본 경로를 api/v1로 변경
public class PartyController {

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    // 파티 완료 처리 (POST 요청)
    @PostMapping("/party/complete/{id}")  // URL에서 id를 경로 변수로 받음
    public ResponseEntity<?> completeParty(@PathVariable int id, @RequestBody PartyCompleteRequest partyCompleteRequest) {
        return partyService.partyComplete(id, partyCompleteRequest);
    }

    // 특정 kakao_user_id로 관련된 파티 삭제 (DELETE 요청)
    @DeleteMapping("kakaouser/meetlist/{kakao_user_id}")  // URL 경로를 api/v1/meetlist/{kakao_user_id}로 변경
    public ResponseEntity<?> deleteParty(@PathVariable("kakao_user_id") int kakaoUserId) {
        try {
            partyService.deletePartyByKakaoUserId(kakaoUserId);
            return ResponseEntity.noContent().build();  // 성공 시 No Content 반환
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 에러 발생 시 Bad Request 반환
        }
    }
}
