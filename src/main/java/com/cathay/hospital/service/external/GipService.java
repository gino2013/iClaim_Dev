package com.cathay.hospital.service.external;

import org.springframework.stereotype.Service;

/**
 * GIP服務介面
 */
@Service
public interface GipService {
    String getTenantMapping(String tenantId);
} 