package com.cathay.hospital.service.external.mock;

import com.cathay.hospital.service.external.GipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * GIP服務的 Mock 實作
 */
@Service
@Profile("!prod")
public class MockGipServiceImpl implements GipService {
    private static final Logger log = LoggerFactory.getLogger(MockGipServiceImpl.class);
    
    @Override
    public String getTenantMapping(String tenantId) {
        log.info("Mock GIP tenant mapping for: {}", tenantId);
        return "ct-03374707-mock";
    }
} 