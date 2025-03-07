package com.cathay.hospital.controller;

import com.cathay.hospital.exception.BusinessException;
import com.cathay.hospital.model.ApiResponse;
import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.service.OffsetCaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 抵繳案件控制器
 *
 * <p>提供抵繳案件相關的 REST API 端點，包括案件新增及試算功能。
 * 主要處理 HTTP 請求的接收和響應，並調用相應的服務層方法進行業務處理。
 *
 * <p>API 端點列表：
 * <ul>
 *   <li>POST /v1/case/add-doc-cal - 案件新增及試算</li>
 * </ul>
 *
 * <p>錯誤處理：
 * <ul>
 *   <li>0001 - 輸入欄位缺漏或格式有誤</li>
 *   <li>E001 - 資料不存在無法修改</li>
 *   <li>E002 - 資料重複無法新增</li>
 *   <li>A001 - GIP查無資料</li>
 * </ul>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@RestController
@RequestMapping("/v1/case")
public class OffsetCaseController {
    private static final Logger log = LoggerFactory.getLogger(OffsetCaseController.class);

    @Autowired
    private OffsetCaseService offsetCaseService;

    /**
     * 案件新增及試算
     *
     * <p>接收案件新增請求，進行必要的檢核後，新增案件並進行試算。
     * 處理流程包括：
     * <ol>
     *   <li>檢核必要欄位</li>
     *   <li>檢查案件是否存在</li>
     *   <li>檢查抵繳名單</li>
     *   <li>新增案件</li>
     *   <li>進行試算</li>
     * </ol>
     *
     * @param headers HTTP 請求標頭，必須包含 TXNSEQ 和 TENANT_ID
     * @param request 案件新增請求資料
     * @return API 響應，包含試算結果或錯誤信息
     */
    @PostMapping("/add-doc-cal")
    public ResponseEntity<ApiResponse> addDocumentAndCalculate(
            @RequestHeader Map<String, String> headers,
            @RequestBody OffsetCaseRequest request) {
        log.info("Received headers: {}", headers);
        log.info("Available header names: {}", headers.keySet());
        log.info("Received request body: {}", request);
        
        try {
            CalculationResult result = offsetCaseService.processCase(headers, request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (BusinessException e) {
            log.error("Business error occurred: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            return ResponseEntity.ok(ApiResponse.error("9999", "系統發生未預期的錯誤"));
        }
    }

    @PostMapping("/process")
    public ResponseEntity<CalculationResult> processCase(
            @RequestHeader Map<String, String> headers,
            @RequestBody OffsetCaseRequest request) {
        log.info("Received headers: {}", headers);
        log.info("Received request body: {}", request);
        log.info("Request fields:");
        log.info("- organizationId: [{}]", request.getOrganizationId());
        log.info("- insuredName: [{}]", request.getInsuredName());
        log.info("- insuredId: [{}]", request.getInsuredId());
        log.info("- charNo: [{}]", request.getCharNo());
        log.info("- admissionNo: [{}]", request.getAdmissionNo());
        log.info("- admissionDate: [{}]", request.getAdmissionDate());
        log.info("- updateId: [{}]", request.getUpdateId());

        try {
            CalculationResult result = offsetCaseService.processCase(headers, request);
            return ResponseEntity.ok(result);
        } catch (BusinessException e) {
            log.error("Business error occurred: {}", e.getMessage(), e);
            throw e;
        }
    }
} 