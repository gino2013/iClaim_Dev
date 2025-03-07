package com.cathay.hospital.controller;

import com.cathay.hospital.exception.BusinessException;
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
     * 新增案件並進行試算
     *
     * @param headers HTTP 請求標頭
     * @param request 案件請求資料
     * @return 試算結果
     */
    @PostMapping("/add-doc-cal")
    public ResponseEntity<?> addDocumentAndCalculate(
            @RequestHeader Map<String, String> headers,
            @RequestBody OffsetCaseRequest request) {
        try {
            log.info("Received case processing request: {}", request);
            CalculationResult result = offsetCaseService.processCase(headers, request);
            log.info("Case processed successfully");
            return ResponseEntity.ok(result);
        } catch (BusinessException e) {
            log.error("Business error occurred: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            return ResponseEntity.internalServerError().body("系統錯誤");
        }
    }

    /**
     * 查詢案件狀態
     *
     * @param caseNo 案件編號
     * @return 案件資訊
     */
    @GetMapping("/{caseNo}")
    public ResponseEntity<?> getCaseStatus(@PathVariable String caseNo) {
        try {
            log.info("Retrieving case status for case number: {}", caseNo);
            // TODO: 實作案件狀態查詢
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            log.error("Business error occurred while retrieving case status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while retrieving case status", e);
            return ResponseEntity.internalServerError().body("系統錯誤");
        }
    }
} 