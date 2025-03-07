package com.cathay.hospital.service.external.mock;

import com.cathay.hospital.service.external.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 文件服務的 Mock 實作
 */
@Slf4j
@Service
@Profile("!prod")
public class MockDocumentServiceImpl implements DocumentService {
    
    @Override
    public void uploadDocument(String caseNo, byte[] content) {
        log.info("Mock document upload for case: {}, content size: {}", 
                caseNo, content != null ? content.length : 0);
    }
} 