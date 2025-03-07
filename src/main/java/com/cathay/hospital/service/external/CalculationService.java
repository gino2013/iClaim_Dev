package com.cathay.hospital.service.external;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;

/**
 * 試算服務介面
 */
public interface CalculationService {
    /**
     * 調用試算服務
     * @param caseNo 案件編號
     * @param request 試算請求
     * @return 試算結果
     */
    CalculationResult calculate(String caseNo, OffsetCaseRequest request);
} 