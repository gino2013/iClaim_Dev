package com.cathay.hospital.service;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.external.CalculationService;
import com.cathay.hospital.service.external.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.beans.factory.annotation.Qualifier;

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
class OffsetCaseServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    @Qualifier("mockCalculationServiceImpl")
    private CalculationService calculationService;

    @Mock
    private DocumentService documentService;

    private OffsetCaseService offsetCaseService;

    @BeforeEach
    void setUp() {
        offsetCaseService = new OffsetCaseService();
        ReflectionTestUtils.setField(offsetCaseService, "jdbcTemplate", jdbcTemplate);
        ReflectionTestUtils.setField(offsetCaseService, "calculationService", calculationService);
        ReflectionTestUtils.setField(offsetCaseService, "documentService", documentService);
        ReflectionTestUtils.setField(offsetCaseService, "environment", "UT");
    }

    @SuppressWarnings("unchecked")
    @Test
    void processDocuments_Success() {
        // Arrange
        Map<String, Object> input = new HashMap<>();
        input.put("ADMISSION_NO", "ADM001");
        input.put("UPDATE_ID", "USER001");

        List<Map<String, Object>> patterns = new ArrayList<>();
        Map<String, Object> pattern = new HashMap<>();
        pattern.put("pattern_id", "1");
        pattern.put("pattern_name", "Test Pattern");
        pattern.put("paper_seq", "001");
        pattern.put("required_flag", "Y");
        pattern.put("status_code", "1");
        patterns.add(pattern);

        when(jdbcTemplate.queryForList(any(String.class), eq("O")))
            .thenReturn(patterns);

        List<String> bundleSeqs = List.of("BUNDLE001");
        when(jdbcTemplate.queryForList(any(String.class), eq(String.class), any(Object[].class)))
            .thenReturn(bundleSeqs)
            .thenReturn(bundleSeqs);

        // Act
        List<Map<String, String>> result = (List<Map<String, String>>) ReflectionTestUtils.invokeMethod(
            offsetCaseService, 
            "processDocuments", 
            input
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("001", result.get(0).get("xPaperSeq"));
        assertEquals("BUNDLE001", result.get(0).get("xBatchId"));
        assertEquals("Test Pattern", result.get(0).get("xPaperName"));
        assertEquals("USER001", result.get(0).get("UPDATE_ID"));
    }

    @Test
    void processCase_Success() {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("TENANT_ID", "TEST");
        headers.put("TXNSEQ", "123456");
        
        OffsetCaseRequest request = new OffsetCaseRequest();
        request.setAdmissionNo("ADM001");
        request.setInsuredId("A123456789");
        request.setDocument("base64EncodedDocument");
        request.setPolicyNo("POL001");
        request.setInsuredName("测试");
        request.setOffsetAmount(new BigDecimal("1000.00"));
        request.setOrganizationId("ORG001");
        request.setCharNo("CHAR001");
        request.setAdmissionDate("2025-03-07");
        request.setUpdateId("USER001");
        
        when(jdbcTemplate.queryForObject(
            any(String.class), 
            eq(String.class), 
            any(Object[].class)
        )).thenReturn("TEST_TENANT");
            
        when(jdbcTemplate.queryForObject(
            any(String.class), 
            eq(Integer.class), 
            any(Object[].class)
        )).thenReturn(0);  // Case not exists
            
        CalculationResult expectedResult = CalculationResult.builder()
            .calculatedAmount(new BigDecimal("1000.00"))
            .calculationReason("正常试算")
            .build();
            
        when(calculationService.calculate(any(), any())).thenReturn(expectedResult);
        
        // Act
        CalculationResult result = offsetCaseService.processCase(headers, request);
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getCalculatedAmount());
        assertEquals("正常试算", result.getCalculationReason());
    }
} 