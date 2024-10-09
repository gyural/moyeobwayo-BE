//package com.moyeobwayo.moyeobwayo.User;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.moyeobwayo.moyeobwayo.Controller.UserController;
//import com.moyeobwayo.moyeobwayo.Domain.Party;
//import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
//import com.moyeobwayo.moyeobwayo.Domain.request.user.UserLoginRequest;
//import com.moyeobwayo.moyeobwayo.Service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.hamcrest.Matchers.*;
//
//@WebMvcTest(UserController.class)  // UserController만 테스트
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;  // JSON 직렬화/역직렬화를 위한 ObjectMapper
//
//    private UserEntity testUser;
//    private Party testParty;
//
//    @BeforeEach
//    public void setUp() {
//        // 테스트용 파티 객체 설정
//        testParty = new Party();
//        testParty.setParty_id(1);
//        testParty.setParty_name("Test Party");
//
//        // 테스트용 사용자 객체 설정
//        testUser = new UserEntity();
//        testUser.setUser_id(1);
//        testUser.setUser_name("testUser");
//        testUser.setPassword("password123");
//        testUser.setParty(testParty);  // 파티 연결
//    }
//
//    @Test
//    public void loginUser_Success() throws Exception {
//        // given: 서비스가 해당 요청을 처리할 때 기대되는 결과 설정
//        UserLoginRequest loginRequest = new UserLoginRequest();
//        loginRequest.setUserName("testUser");
//        loginRequest.setPassword("password123");
//        loginRequest.setPartyId(1);
//
//        // Mock UserService의 로그인 메서드가 testUser를 반환하도록 설정
//        Mockito.when(userService.login("testUser", "password123", 1)).thenReturn(Optional.of(testUser));
//
//        // when & then: MockMvc로 POST 요청을 보낸 후 기대 결과 검증
//        mockMvc.perform(post("/api/v1/user/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))  // JSON 직렬화 후 요청 본문 설정
//                .andExpect(status().isOk())  // 응답 상태가 200 OK인지 확인
//                .andExpect(jsonPath("$.user_id", is(testUser.getUser_id())))  // 반환된 JSON의 user_id 필드 검증
//                .andExpect(jsonPath("$.user_name", is(testUser.getUser_name())))
//                .andExpect(jsonPath("$.party.party_id", is(testParty.getParty_id())));  // 파티 ID 확인
//    }
//
//    @Test
//    public void loginUser_Failure_DuplicateUsername() throws Exception {
//        // given: 중복된 사용자 이름이 있는 경우 (서비스가 빈 Optional 반환)
//        UserLoginRequest loginRequest = new UserLoginRequest();
//        loginRequest.setUserName("duplicateUser");
//        loginRequest.setPassword("password123");
//        loginRequest.setPartyId(1);
//
//        // Mock UserService의 로그인 메서드가 빈 Optional을 반환하도록 설정
//        Mockito.when(userService.login("duplicateUser", "password123", 1)).thenReturn(Optional.empty());
//
//        // when & then: MockMvc로 POST 요청을 보낸 후 기대 결과 검증
//        mockMvc.perform(post("/api/v1/user/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isUnauthorized())  // 응답 상태가 401 Unauthorized인지 확인
//                .andExpect(jsonPath("$").value("Login failed: Duplicate username in the same party or invalid credentials"));  // 반환된 메시지 확인
//    }
//}
