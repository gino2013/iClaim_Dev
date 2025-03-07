package com.cathay.hospital.service.external.impl;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.external.CalculationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service()
public class CalculationServiceImpl implements CalculationService {
    private static final Logger log = LoggerFactory.getLogger(CalculationServiceImpl.class);
    
    @Value("${external.api.calculation.url}")
    private String calculationUrl;
    
    @Value("${spring.profiles.active:}")
    private String activeProfile;  // 注入当前激活的 profile
    
    // private final RestTemplate restTemplate;
    
    // public CalculationServiceImpl(RestTemplate restTemplate) {
    //     this.restTemplate = restTemplate;
    // }
    
    /**
     * 调用试算服务
     * @param caseNo 案件编号
     * @param request 试算请求
     * @return 试算结果
     */
    @Override
    public CalculationResult calculate(String caseNo, OffsetCaseRequest request) {
        // 在开发环境返回模拟数据
        if ("dev".equals(activeProfile)) {
            return CalculationResult.builder()
                    .calculatedAmount(new BigDecimal("1000.00"))
                    .calculationReason("模拟计算结果")
                    .build();
        }
        
        // 实际计算逻辑
        return CalculationResult.builder()
                .calculatedAmount(new BigDecimal("500.00"))
                .calculationReason("正常计算")
                .build();
    }
} 