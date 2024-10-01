package com.moyeobwayo.moyeobwayo.Controller;

import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moyeobwayo.moyeobwayo.Domain.request.user.UserLoginRequest;


@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    // 생성자를 통한 서비스 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request) {
        UserEntity user = userService.login(request.getUserName(), request.getPassword());
        if (user != null) {
            // 로그인 성공
            return ResponseEntity.ok(user);
        } else {
            // 로그인 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: Invalid username or password");
        }
    }
}
