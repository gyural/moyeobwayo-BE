package com.moyeobwayo.moyeobwayo.Controller;

import com.moyeobwayo.moyeobwayo.Domain.KakaoProfile;
import com.moyeobwayo.moyeobwayo.Domain.request.KakaoUserCreateRequest;
import com.moyeobwayo.moyeobwayo.Service.KakaoUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/kakaoUser")
public class KakaoUserController {
    private final KakaoUserService kakaoUserService;

    public KakaoUserController(KakaoUserService kakaoUserService) {
        this.kakaoUserService = kakaoUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createKakaoUser(@RequestBody KakaoUserCreateRequest request) {
        KakaoProfile profile = kakaoUserService.createUser(request.getCode());
        return ResponseEntity.ok(profile);
    }
}
