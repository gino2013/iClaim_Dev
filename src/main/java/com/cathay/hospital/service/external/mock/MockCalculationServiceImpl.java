package com.cathay.hospital.service.external.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.external.CalculationService;

import java.math.BigDecimal;

/**
 * 試算服務的 Mock 實作
 */
@Service()
@Profile("dev")  // 只在開發環境使用 mock
public class MockCalculationServiceImpl implements CalculationService {
    private static final Logger log = LoggerFactory.getLogger(MockCalculationServiceImpl.class);
    
    @Override
    public CalculationResult calculate(String caseNo, OffsetCaseRequest request) {
        log.info("Mock calculation for case: {}", caseNo);
        return CalculationResult.builder()
                .calculatedAmount(new BigDecimal("1000.00"))
                .calculationReason("Mock calculation result")
                .build();
    }
} 