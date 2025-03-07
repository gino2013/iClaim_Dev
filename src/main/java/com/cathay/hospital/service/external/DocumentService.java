package com.cathay.hospital.service.external;

import org.springframework.stereotype.Service;

/**
 * 文件服務介面
 */
@Service
public interface DocumentService {
    String uploadDocument(String document);
} 