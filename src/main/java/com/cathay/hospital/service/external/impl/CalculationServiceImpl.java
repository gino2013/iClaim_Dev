package com.cathay.hospital.service.external.impl;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.external.CalculationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import java.math.BigDecimal;

@Service
@Primary
public class CalculationServiceImpl implements CalculationService {
    private static final Logger log = LoggerFactory.getLogger(CalculationServiceImpl.class);
    
    @Value("${spring.profiles.active:}")
    private String activeProfile;
    
    // private final RestTemplate restTemplate;
    
    // public CalculationServiceImpl(RestTemplate restTemplate) {
    //     this.restTemplate = restTemplate;
    // }
    
    /**
     * 調用試算服務
     * @param caseNo 案件編號
     * @param request 試算請求
     * @return 試算結果
     */
    @Override
    public CalculationResult calculate(String caseNo, OffsetCaseRequest request) {
        log.info("Calculating for case: {}", caseNo);
        
        // 在開發環境返回模擬資料
        if ("dev".equals(activeProfile)) {
            return CalculationResult.builder()
                    .caseNo(caseNo)
                    .calculatedAmount(new BigDecimal("1000.00"))
                    .calculationReason("模擬計算結果")
                    .build();
        }
        
        // 實際計算邏輯
        return CalculationResult.builder()
                .caseNo(caseNo)
                .calculatedAmount(new BigDecimal("500.00"))
                .calculationReason("正常計算")
                .build();
    }
} 