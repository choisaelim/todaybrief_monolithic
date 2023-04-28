package com.example.user;

import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.user.dto.LoginRequest;
import com.example.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class MapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    private String token;

    private LoginRequest loginUser() {
        String userName = "guest";
        String password = "1234567";
        LoginRequest userDto = new LoginRequest();
        userDto.setUsername(userName);
        userDto.setPassword(password);
        return userDto;
    }

    @Test
    public void 로그인() throws Exception {

        LoginRequest user = loginUser();
        String content = objectMapper.writeValueAsString(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        byte[] response = mvcResult.getResponse().getContentAsByteArray();
        JSONObject jsonResponse = new JSONObject(new String(response));
        // 로그인 성공시 토큰을 담는다.
        token = jsonResponse.get("accessToken").toString();
        assertTrue(!"".equals(token));
    }

}
