package com.moyeobwayo.moyeobwayo.party;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Domain.request.party.PartyCompleteRequest;
import com.moyeobwayo.moyeobwayo.Repository.PartyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
public class PartyTest {

    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;  // JSON 직렬화를 위한 ObjectMapper

    @Test
    public void testCompleteParty_MissingRequiredValues() throws Exception {
        // 필수값 누락된 요청 생성
        PartyCompleteRequest request = new PartyCompleteRequest();
        request.setUserId("");  // userId 누락
        request.setCompleteTime(null);  // completeTime 누락

        // 요청 실행 및 검증
        mockMvc.perform(post("/api/v1/party/complete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // 요청 직렬화
                .andExpect(status().isBadRequest())  // 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Error: User ID is required."));  // JSON 응답 검증
    }
}
