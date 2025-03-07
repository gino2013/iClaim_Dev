package com.cathay.hospital.service.external.mock;

import com.cathay.hospital.service.external.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

/**
 * 文件服務的 Mock 實作
 */
@Service
@Profile("!prod")
public class MockDocumentServiceImpl implements DocumentService {
    private static final Logger log = LoggerFactory.getLogger(MockDocumentServiceImpl.class);
    
    @Override
    public String uploadDocument(String document) {
        log.info("Mock document upload: {}", document);
        return "MOCK_DOCUMENT_ID";
    }
} 