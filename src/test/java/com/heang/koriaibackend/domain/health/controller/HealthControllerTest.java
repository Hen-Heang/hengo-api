package com.heang.koriaibackend.domain.health.controller;

import com.heang.koriaibackend.domain.health.service.HealthReadinessService;
import com.heang.koriaibackend.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@AutoConfigureMockMvc(addFilters = false)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private HealthReadinessService healthReadinessService;

    @Test
    void health_ShouldReturnUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(200))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("koriai-backend"));
    }

    @Test
    void readiness_ShouldReturnReady_WhenDatabaseIsAvailable() throws Exception {
        when(healthReadinessService.isDatabaseReady()).thenReturn(true);

        mockMvc.perform(get("/api/health/ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(200))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.database").value("UP"));
    }

    @Test
    void readiness_ShouldReturnServiceUnavailable_WhenDatabaseIsUnavailable() throws Exception {
        when(healthReadinessService.isDatabaseReady()).thenReturn(false);

        mockMvc.perform(get("/api/health/ready"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status.code").value(5001))
                .andExpect(jsonPath("$.data.status").value("NOT_READY"))
                .andExpect(jsonPath("$.data.database").value("DOWN"));
    }
}
