package com.cathay.hospital.service.external.mock;

import com.cathay.hospital.service.external.GipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * GIP服務的 Mock 實作
 */
@Slf4j
@Service
@Profile("!prod")
public class MockGipServiceImpl implements GipService {
    
    @Override
    public String getTenantMapping(String tenantId) {
        log.info("Mock GIP tenant mapping for: {}", tenantId);
        return "ct-03374707-mock";
    }
} 