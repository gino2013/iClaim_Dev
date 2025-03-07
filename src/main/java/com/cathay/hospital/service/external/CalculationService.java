package com.cathay.hospital.service.external;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import org.springframework.stereotype.Service;

/**
 * 試算服務介面
 */
@Service
public interface CalculationService {
    CalculationResult calculate(String caseNo, OffsetCaseRequest request);
} 