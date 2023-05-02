package com.example.user;

import static org.junit.Assert.assertTrue;

import java.io.ObjectOutputStream.PutField;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.user.dto.LoginRequest;
import com.example.user.dto.RequestUser;
import com.example.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class MapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    private String token;

    private RequestUser newUser() {
        RequestUser user = new RequestUser();
        user.setUserId("user1");
        user.setPassword("1234567");
        user.setEmail("");
        user.setUserName("홍길동");
        return user;
    }

    private LoginRequest loginUser() {
        String userName = "guest";
        String password = "1234567";
        LoginRequest userDto = new LoginRequest();
        userDto.setUsername(userName);
        userDto.setPassword(password);
        return userDto;
    }

    private UserDto guestUser() {
        UserDto user = new UserDto();
        user.setUserId("guest");
        return user;
    }

    public enum STATUS {
        CREATE,
        OK
    }

    public enum HTTP_METHOD {
        POST,
        PUT,
        GET
    }

    public enum RESULT_TYPE {
        OBJECT,
        ARRAY
    }

    private byte[] mockResult(String url, Object o, HTTP_METHOD method, STATUS status) throws Exception {
        String content = objectMapper.writeValueAsString(o);

        MockHttpServletRequestBuilder mockBuilder = null;
        ResultMatcher mockMatchers = null;

        switch (method) {
            case POST:
                mockBuilder = MockMvcRequestBuilders.post(url);
                break;
            case GET:
                mockBuilder = MockMvcRequestBuilders.get(url);
                break;
            case PUT:
                mockBuilder = MockMvcRequestBuilders.put(url);
                break;
        }

        switch (status) {
            case OK:
                mockMatchers = MockMvcResultMatchers.status().isOk();
                break;
            case CREATE:
                mockMatchers = MockMvcResultMatchers.status().isCreated();
                break;
        }

        if (token != null) {
            mockBuilder = mockBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        MvcResult mvcResult = mockMvc.perform(mockBuilder
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(mockMatchers)
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        byte[] response = mvcResult.getResponse().getContentAsByteArray();
        return response;
    }

    private JSONObject getMockJsonObjectResult(String url, Object o, HTTP_METHOD method, STATUS status)
            throws Exception {
        byte[] response = mockResult(url, o, method, status);
        JSONObject jsonResponse = new JSONObject(new String(response));
        return jsonResponse;
    }

    private JSONArray getMockJsonArrayResult(String url, Object o, HTTP_METHOD method, STATUS status) throws Exception {
        byte[] response = mockResult(url, o, method, status);
        JSONArray jsonResponse = new JSONArray(new String(response));
        return jsonResponse;
    }

    @Test
    public void 회원가입() throws Exception {
        RequestUser user = newUser();

        JSONObject jsonResponse = getMockJsonObjectResult("/users", user, HTTP_METHOD.POST, STATUS.CREATE);
        // 회원가입 후 result의 id가 가입 요청 아이디와 같음
        assertTrue(user.getUserId().equals(jsonResponse.get("userId").toString()));
    }

    @Test
    public void 로그인() throws Exception {
        LoginRequest user = loginUser();

        JSONObject jsonResponse = getMockJsonObjectResult("/login", user, HTTP_METHOD.POST, STATUS.OK);
        // 로그인 성공시 토큰을 담는다.
        token = jsonResponse.get("accessToken").toString();
        assertTrue(!"".equals(token));
    }

    @Test
    public void 날씨조회() throws Exception {
        로그인();
        UserDto user = guestUser();
        JSONArray jsonResponse = getMockJsonArrayResult("/map/weather/" + user.getUserId(), user, HTTP_METHOD.GET,
                STATUS.OK);

        assertTrue(jsonResponse.length() > 0 && !"".equals(((JSONObject) jsonResponse.get(0)).get("message")));
    }

    @Test
    public void 차량소요시간조회() throws Exception {
        로그인();
        UserDto user = guestUser();
        JSONArray jsonResponse = getMockJsonArrayResult("/map/car/" + user.getUserId(), user, HTTP_METHOD.GET,
                STATUS.OK);

        assertTrue(jsonResponse.length() > 0 && !"".equals(((JSONObject) jsonResponse.get(0)).get("message")));
    }
}
