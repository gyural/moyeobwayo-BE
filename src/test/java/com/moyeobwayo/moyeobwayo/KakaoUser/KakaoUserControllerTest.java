package com.moyeobwayo.moyeobwayo.KakaoUser;

import com.moyeobwayo.moyeobwayo.Controller.KakaoUserController;
import com.moyeobwayo.moyeobwayo.Domain.KakaoProfile;
import com.moyeobwayo.moyeobwayo.Domain.request.KakaoUserCreateRequest;
import com.moyeobwayo.moyeobwayo.Service.KakaoUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(KakaoUserController.class)  // Controller 테스트를 위한 어노테이션
public class KakaoUserControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc를 이용한 Controller 테스트

    @MockBean
    private KakaoUserService kakaoUserService;  // Mock 서비스

    private ObjectMapper objectMapper = new ObjectMapper();

    private KakaoUserCreateRequest request;

    @BeforeEach
    void setUp() {
        request = new KakaoUserCreateRequest();
        request.setCode("dummy-code");
    }

    @Test
    void testCreateKakaoUser() throws Exception {
        KakaoProfile profile = new KakaoProfile();
        profile.setKakao_user_id(1234567890L);
        profile.setNickname("Test User");

        // Mocking: 서비스 호출 시 해당 프로필 객체를 반환하도록 설정
        Mockito.when(kakaoUserService.createUser(Mockito.anyString())).thenReturn(profile);

        // 실제 요청 테스트
        mockMvc.perform(post("/api/v1/kakaoUser/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // JSON으로 변환하여 전송
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kakao_user_id").value(1234567890L))
                .andExpect(jsonPath("$.nickname").value("Test User"));
    }
}
