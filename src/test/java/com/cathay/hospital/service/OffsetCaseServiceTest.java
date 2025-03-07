package com.cathay.hospital.service;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.repository.OffsetCaseRepository;
import com.cathay.hospital.service.external.CalculationService;
import com.cathay.hospital.service.external.DocumentService;
import jakarta.persistence.EntityManager;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OffsetCaseServiceTest {

    @Mock
    private OffsetCaseRepository caseRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CalculationService calculationService;

    private OffsetCaseService offsetCaseService;

    @BeforeEach
    void setUp() {
        offsetCaseService = new OffsetCaseService(
            caseRepository,      // 使用建構子注入
            jdbcTemplate,
            entityManager,
            calculationService
        );
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
        
        OffsetCaseRequest request = new OffsetCaseRequest();
        request.setAdmissionNo("ADM001");
        request.setInsuredId("A123456789");
        request.setDocument("base64EncodedDocument");
        request.setPolicyNo("POL001");
        request.setInsuredName("測試");
        request.setOffsetAmount(new BigDecimal("1000.00"));
        
        // Mock 數據庫檢查相關的查詢
        when(jdbcTemplate.queryForObject(
            eq("SELECT current_database()"), 
            eq(String.class)
        )).thenReturn("test_db");
        
        when(jdbcTemplate.queryForObject(
            contains("SELECT EXISTS"), 
            eq(Boolean.class)
        )).thenReturn(true);
        
        when(jdbcTemplate.queryForObject(
            eq("SELECT COUNT(*) FROM public.offset_case"), 
            eq(Integer.class)
        )).thenReturn(0);
        
        // Mock 其他必要的數據庫操作
        when(jdbcTemplate.queryForObject(
            anyString(), 
            eq(Integer.class), 
            any(Object[].class)
        )).thenReturn(0);  // 假設案件不存在
        
        CalculationResult expectedResult = CalculationResult.builder()
            .caseNo("TEST001")
            .calculatedAmount(new BigDecimal("1000.00"))
            .calculationReason("正常計算")
            .build();
            
        when(calculationService.calculate(any(), any())).thenReturn(expectedResult);
        
        // Act
        CalculationResult result = offsetCaseService.processCase(headers, request);
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getCalculatedAmount());
        assertEquals("正常計算", result.getCalculationReason());
    }
} 