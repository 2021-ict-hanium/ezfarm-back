package com.ezfarm.ezfarmback.common.advice;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ezfarm.ezfarmback.common.exception.dto.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@DisplayName("예외 처리 테스트")
@SpringBootTest
public class GlobalExceptionHandlerTest {

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();

        objectMapper = new ObjectMapper();
    }

    @DisplayName("잘못된 타입의 요청 값이 왔을 때 예외 처리를 한다.")
    @Test
    void methodArgumentTypeMismatchException() throws Exception {
        mockMvc.perform(get("/test/path"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("C_002"))
            .andDo(print());
    }

    @DisplayName("적절하지 않은 HTTP 메소드 요청이 왔을 때 예외 처리를 한다.")
    @Test
    void handleHttpRequestMethodNotSupportedException() throws Exception {
        mockMvc.perform(delete("/test"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.code").value("C_003"))
            .andDo(print());
    }

    @DisplayName("커스텀 예외를 처리한다.")
    @Test
    void handleCustomException() throws Exception {
        mockMvc.perform(post("/test"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATED_EMAIL.getCode()))
            .andDo(print());
    }
}
