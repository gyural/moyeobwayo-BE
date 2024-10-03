package com.moyeobwayo.moyeobwayo.Controller;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Service.KakaoUserPartyService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/KakaoUser")
public class KakaoUserPartyController {

    private final KakaoUserPartyService kakaoUserPartyService;
    @Autowired
    public KakaoUserPartyController(KakaoUserPartyService kakaoUserPartyService) {
        this.kakaoUserPartyService = kakaoUserPartyService;
    }
    @Getter
    @Setter
    public static class ReqData {
        private int kakaoUserId;

        // 기본 생성자 추가
        public ReqData() {}

        public ReqData(int kakaoUserId) {
            this.kakaoUserId = kakaoUserId;
        }
    }
    @PostMapping("/meetlist")
    public ResponseEntity<?> getPartyByKakaoUserId(@RequestBody ReqData reqData) {
        try {
            // 서비스에서 kakao_user_id로 Party 조회
            int KakaoUserId = reqData.getKakaoUserId();
            List<Party> parties = kakaoUserPartyService.getPartyByKakaoUserId(KakaoUserId);
            return ResponseEntity.ok(parties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }
}
