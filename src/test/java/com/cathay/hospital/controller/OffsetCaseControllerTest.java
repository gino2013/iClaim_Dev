package com.cathay.hospital.controller;

import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.OffsetCaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OffsetCaseController.class)
class OffsetCaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OffsetCaseService offsetCaseService;

    @Test
    void testAddDocumentAndCalculate_ValidRequest() throws Exception {
        // Arrange
        OffsetCaseRequest request = new OffsetCaseRequest();
        request.setAdmissionNo("ADM001");
        request.setInsuredId("A123456789");
        request.setDocument("base64EncodedDocument");
        request.setPolicyNo("POL001");
        request.setInsuredName("测试");
        request.setOffsetAmount(new BigDecimal("1000.00"));
        
        CalculationResult expectedResult = CalculationResult.builder()
            .calculatedAmount(new BigDecimal("1000.00"))
            .calculationReason("正常试算")
            .build();
            
        when(offsetCaseService.processCase(any(), any())).thenReturn(expectedResult);

        // Act & Assert
        mockMvc.perform(post("/v1/case/add-doc-cal")
                .contentType(MediaType.APPLICATION_JSON)
                .header("TENANT_ID", "TEST")
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnCode").value("0000"))
                .andExpect(jsonPath("$.returnData.calculatedAmount").value("1000.00"));
    }
} 