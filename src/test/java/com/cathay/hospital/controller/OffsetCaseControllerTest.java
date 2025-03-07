package com.cathay.hospital.controller;

import com.cathay.hospital.model.OffsetCaseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OffsetCaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddDocumentAndCalculate_MissingFields() throws Exception {
        // 準備測試數據
        OffsetCaseRequest request = OffsetCaseRequest.builder()
                .organizationId("")  // 空值測試
                .insuredName("Test Name")
                .insuredId("A123456789")
                .charNo("CHAR001")
                .admissionNo("ADM001")
                .admissionDate("2023-01-01")
                .updateId("USER001")
                .build();

        Map<String, String> headers = new HashMap<>();
        headers.put("TXNSEQ", "123456");
        // 故意不加 TENANT_ID 來測試錯誤情況

        // 執行測試
        mockMvc.perform(post("/v1/case/add-doc-cal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("TXNSEQ", headers.get("TXNSEQ")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnCode").value("0001"))
                .andExpect(jsonPath("$.returnDesc").value("輸入欄位缺漏或格式有誤"));
    }

    @Test
    public void testAddDocumentAndCalculate_ValidRequest() throws Exception {
        // 準備測試數據
        OffsetCaseRequest request = OffsetCaseRequest.builder()
                .organizationId("ORG001")
                .insuredName("Test Name")
                .insuredId("A123456789")
                .charNo("CHAR001")
                .admissionNo("ADM001")
                .admissionDate("2023-01-01")
                .updateId("USER001")
                .build();

        // 執行測試
        mockMvc.perform(post("/v1/case/add-doc-cal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("TXNSEQ", "123456")
                .header("TENANT_ID", "UT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnCode").exists())
                .andExpect(jsonPath("$.returnDesc").exists());
    }
} 