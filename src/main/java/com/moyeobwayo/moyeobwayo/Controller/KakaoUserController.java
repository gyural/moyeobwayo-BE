package com.moyeobwayo.moyeobwayo.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/kakaouser")
public class KakaoUserController {
    @GetMapping("/meetlist")
    public void Hello(@RequestBody String message) {
        System.out.println("안녕 " + message);}
}
