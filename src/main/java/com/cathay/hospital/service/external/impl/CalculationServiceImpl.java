package com.cathay.hospital.service.external.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.external.CalculationService;
import com.cathay.hospital.exception.BusinessException;

@Service
@Primary
@Profile("!dev")  // 非開發環境使用實際實作
public class CalculationServiceImpl implements CalculationService {
    private static final Logger log = LoggerFactory.getLogger(CalculationServiceImpl.class);
    
    private final RestTemplate restTemplate;
    private final String calculationUrl;
    
    public CalculationServiceImpl(
            @Value("${external.api.calculation.url}") String calculationUrl) {
        this.restTemplate = new RestTemplate();
        this.calculationUrl = calculationUrl;
    }
    
    @Override
    public CalculationResult calculate(String caseNo, OffsetCaseRequest request) {
        try {
            return restTemplate.postForObject(
                calculationUrl,
                request,
                CalculationResult.class
            );
        } catch (RestClientException e) {
            log.error("Calculation API error", e);
            throw new BusinessException("A007", "呼叫試算服務API發生錯誤");
        }
    }
} 