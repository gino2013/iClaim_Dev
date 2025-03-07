package com.cathay.hospital.service.external;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationServiceImpl(restTemplate);
        ReflectionTestUtils.setField(calculationService, "calculationUrl", "http://test-url");
    }

    @Test
    void calculate_Success() {
        // Arrange
        String caseNo = "TEST001";
        OffsetCaseRequest request = new OffsetCaseRequest();
        List<Map<String, String>> documents = new ArrayList<>();
        Map<String, String> doc = new HashMap<>();
        doc.put("xPaperSeq", "001");
        doc.put("xBatchId", "BATCH001");
        documents.add(doc);
        request.setDocuments(documents);

        CalculationResult expectedResponse = CalculationResult.builder()
            .calculatedAmount(new BigDecimal("1000.00"))
            .calculationReason("正常试算")
            .build();

        when(restTemplate.postForObject(
            eq("http://test-url"),
            any(HttpEntity.class),
            eq(CalculationResult.class)
        )).thenReturn(expectedResponse);

        // Act
        CalculationResult result = calculationService.calculate(caseNo, request);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getCalculatedAmount());
        assertEquals("正常试算", result.getCalculationReason());
    }

    @Test
    void calculate_ServiceError() {
        // Arrange
        String caseNo = "TEST001";
        OffsetCaseRequest request = new OffsetCaseRequest();

        when(restTemplate.postForObject(
            eq("http://test-url"),
            any(HttpEntity.class),
            eq(CalculationResult.class)
        )).thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            calculationService.calculate(caseNo, request)
        );

        assertEquals("0005:试算服务调用失败", exception.getMessage());
    }
} 