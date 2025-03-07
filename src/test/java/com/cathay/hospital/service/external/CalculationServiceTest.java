package com.cathay.hospital.service.external;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.external.impl.CalculationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationServiceImpl();
        ReflectionTestUtils.setField(calculationService, "activeProfile", "dev");
    }

    @Test
    void calculate_Success() {
        // Arrange
        String caseNo = "TEST001";
        OffsetCaseRequest request = new OffsetCaseRequest();
        request.setInsuredId("A123456789");
        request.setInsuredName("測試姓名");

        // Act
        CalculationResult result = calculationService.calculate(caseNo, request);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getCalculatedAmount());
        assertEquals("模擬計算結果", result.getCalculationReason());
    }

    @Test
    void calculate_ProductionEnvironment() {
        // Arrange
        String caseNo = "TEST001";
        OffsetCaseRequest request = new OffsetCaseRequest();
        ReflectionTestUtils.setField(calculationService, "activeProfile", "prod");

        // Act
        CalculationResult result = calculationService.calculate(caseNo, request);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getCalculatedAmount());
        assertEquals("正常計算", result.getCalculationReason());
    }
} 