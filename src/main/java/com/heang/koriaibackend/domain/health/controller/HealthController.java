package com.heang.koriaibackend.domain.health.controller;

import com.heang.koriaibackend.common.api.ApiResponse;
import com.heang.koriaibackend.common.api.Code;
import com.heang.koriaibackend.domain.health.service.HealthReadinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthReadinessService healthReadinessService;

    public HealthController(HealthReadinessService healthReadinessService) {
        this.healthReadinessService = healthReadinessService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
                "service", "koriai-backend",
                "status", "UP",
                "timestamp", OffsetDateTime.now().toString()
        ));
    }

    @GetMapping("/ready")
    public ResponseEntity<ApiResponse<Map<String, Object>>> readiness() {
        boolean databaseReady = healthReadinessService.isDatabaseReady();

        Map<String, Object> data = Map.of(
                "service", "koriai-backend",
                "status", databaseReady ? "READY" : "NOT_READY",
                "database", databaseReady ? "UP" : "DOWN",
                "timestamp", OffsetDateTime.now().toString()
        );

        if (databaseReady) {
            return ResponseEntity.ok(ApiResponse.success(data));
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(Code.DATABASE_ERROR, data));
    }
}
