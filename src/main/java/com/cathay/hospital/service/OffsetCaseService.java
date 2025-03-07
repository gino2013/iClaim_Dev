package com.cathay.hospital.service;

import com.cathay.hospital.constant.ErrorCode;
import com.cathay.hospital.constant.StatusCode;
import com.cathay.hospital.entity.OffsetCase;
import com.cathay.hospital.exception.BusinessException;
import com.cathay.hospital.model.CaseInfo;
import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.repository.OffsetCaseRepository;
import com.cathay.hospital.repository.OffsetAllowlistRepository;
import com.cathay.hospital.service.external.CalculationService;
import com.cathay.hospital.service.external.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 抵繳案件服務類
 *
 * <p>提供抵繳案件相關的業務邏輯處理，包括案件新增、檢核和試算等功能。
 * 實現了完整的案件處理流程，包括：
 * <ul>
 *   <li>必要欄位檢核</li>
 *   <li>租戶代碼轉換</li>
 *   <li>案件存在檢查</li>
 *   <li>抵繳名單檢核</li>
 *   <li>案件編號產生</li>
 *   <li>案件新增</li>
 *   <li>試算服務呼叫</li>
 * </ul>
 *
 * <p>處理流程說明：
 * <pre>
 * 1. 檢查必要欄位
 * 2. 取得國泰人壽租戶代碼
 * 3. 檢查案件是否存在
 * 4. 檢查抵繳名單
 * 5. 產生案件編號
 * 6. 新增案件
 * 7. 呼叫試算服務
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Service
public class OffsetCaseService {
    private static final Logger log = LoggerFactory.getLogger(OffsetCaseService.class);

    @Autowired
    private CalculationService calculationService;
    
    @Autowired
    private DocumentService documentService;
    
    @Value("${app.environment}")
    private String environment;

    @Autowired
    private OffsetCaseRepository offsetCaseRepository;
    
    @Autowired
    private OffsetAllowlistRepository allowlistRepository;

    /**
     * 處理抵繳案件的主要方法
     *
     * @param headers HTTP 請求標頭
     * @param request 案件請求資料
     * @return 試算結果
     * @throws BusinessException 當業務邏輯檢核失敗時
     */
    public CalculationResult processCase(Map<String, String> headers, OffsetCaseRequest request) {
        log.info("Processing case with headers: {} and request: {}", headers, request);
        
        // 1. 檢查必要欄位
        validateRequiredFields(headers, request);

        // 2. 取得國泰人壽租戶代碼
        String tenantId = headers.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase("TENANT_ID"))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new BusinessException("0001", "輸入欄位缺漏或格式有誤"));
            
        String ctTenantId = getCtTenantId(tenantId);
        log.info("Resolved ctTenantId: {}", ctTenantId);
        
        // 3. 檢查案件是否存在
        CaseInfo caseInfo = checkExistingCase(request.getAdmissionNo(), ctTenantId);
        
        // 4. 檢查抵繳名單
        if (!caseInfo.isResend()) {
            checkAllowlist(request.getInsuredId(), ctTenantId);
        }

        // 呼叫試算服務
        CalculationResult result = calculationService.calculate(caseInfo.getCaseNo(), request);
        
        // 上傳文件
        documentService.uploadDocument(caseInfo.getCaseNo(), request.getDocument());
        
        return result;
    }

    /**
     * 驗證請求中的必要欄位
     *
     * <p>檢查 HTTP 請求標頭和請求體中的必要欄位是否存在且有值。
     * 必要的標頭欄位包括：
     * <ul>
     *   <li>TXNSEQ - 交易序號</li>
     *   <li>TENANT_ID - 租戶代碼</li>
     * </ul>
     *
     * @param headers HTTP 請求標頭
     * @param request 案件請求資料
     * @throws BusinessException 當有必要欄位缺漏時，拋出代碼為 0001 的異常
     */
    private void validateRequiredFields(Map<String, String> headers, OffsetCaseRequest request) {
        List<String> missingFields = new ArrayList<>();
        
        // 檢查 headers (不區分大小寫)
        boolean hasTxnseq = headers.keySet().stream()
                .anyMatch(key -> key.equalsIgnoreCase("TXNSEQ"));
        boolean hasTenantId = headers.keySet().stream()
                .anyMatch(key -> key.equalsIgnoreCase("TENANT_ID"));
        
        if (!hasTxnseq) {
            log.error("Missing TXNSEQ header");
            missingFields.add("TXNSEQ");
        }
        if (!hasTenantId) {
            log.error("Missing TENANT_ID header");
            missingFields.add("TENANT_ID");
        }
        
        // 檢查 request body
        if (!StringUtils.hasText(request.getOrganizationId())) {
            log.error("Missing organizationId");
            missingFields.add("ORGANIZATION_ID");
        }
        if (!StringUtils.hasText(request.getInsuredName())) {
            log.error("Missing insuredName");
            missingFields.add("INSURED_NAME");
        }
        if (!StringUtils.hasText(request.getInsuredId())) {
            log.error("Missing insuredId");
            missingFields.add("INSURED_ID");
        }
        if (!StringUtils.hasText(request.getCharNo())) {
            log.error("Missing charNo");
            missingFields.add("CHAR_NO");
        }
        if (!StringUtils.hasText(request.getAdmissionNo())) {
            log.error("Missing admissionNo");
            missingFields.add("ADMISSION_NO");
        }
        if (!StringUtils.hasText(request.getAdmissionDate())) {
            log.error("Missing admissionDate");
            missingFields.add("ADMISSION_DATE");
        }
        if (!StringUtils.hasText(request.getUpdateId())) {
            log.error("Missing updateId");
            missingFields.add("UPDATE_ID");
        }

        if (!missingFields.isEmpty()) {
            log.error("Missing fields: {}", missingFields);
            throw new BusinessException("0001", "輸入欄位缺漏或格式有誤");
        }
    }

    /**
     * 獲取當前環境設定
     *
     * @return 環境設定值
     */
    private String getEnvironment() {
        return environment;
    }

    /**
     * 根據租戶代碼獲取對應的國泰人壽租戶代碼
     *
     * <p>根據不同環境返回對應的國泰人壽租戶代碼：
     * <ul>
     *   <li>UT 環境 - ct-03374707-ytzw8</li>
     *   <li>UAT 環境 - ct-03374707-s7jbs</li>
     * </ul>
     *
     * @param tenantId 原始租戶代碼
     * @return 國泰人壽租戶代碼
     * @throws BusinessException 當環境設定不正確時，拋出代碼為 A001 的異常
     */
    private String getCtTenantId(String tenantId) {
        String currentEnv = getEnvironment();
        if (currentEnv == null) {
            log.error("Environment is null");
            throw new BusinessException("A001", "GIP查無資料");
        }
        
        // 轉換為大寫並去除空格進行比較
        currentEnv = currentEnv.trim().toUpperCase();
        log.info("Normalized environment: '{}'", currentEnv);
        
        switch (currentEnv) {
            case "UT":
                log.info("Matching UT environment, returning ct-03374707-ytzw8");
                return "ct-03374707-ytzw8";
            case "UAT":
                log.info("Matching UAT environment, returning ct-03374707-s7jbs");
                return "ct-03374707-s7jbs";
            default:
                log.error("No matching environment found for: '{}'", currentEnv);
                throw new BusinessException("A001", "GIP查無資料");
        }
    }

    /**
     * 檢查案件是否存在並判斷其狀態
     *
     * <p>根據住院號和租戶代碼查詢案件，並根據案件狀態判斷是否可以新增或重送：
     * <ul>
     *   <li>無案件 - 允許新增</li>
     *   <li>CCF 狀態 - 允許重送</li>
     *   <li>HT 狀態 - 允許新增</li>
     *   <li>其他狀態 - 不允許新增</li>
     * </ul>
     *
     * @param admissionNo 住院號
     * @param ctTenantId 國泰人壽租戶代碼
     * @return 案件資訊，包含案件編號和重送標記
     * @throws BusinessException 當案件已存在且不允許新增時
     */
    private CaseInfo checkExistingCase(String admissionNo, String ctTenantId) {
        Optional<OffsetCase> existingCase = offsetCaseRepository
            .findLatestByAdmissionNoAndCtTenantId(admissionNo, ctTenantId);

        if (existingCase.isEmpty()) {
            return CaseInfo.builder()
                    .caseNo(null)
                    .resend(false)
                    .build();
        }

        OffsetCase latestCase = existingCase.get();
        String statusCode = latestCase.getStatusCode();

        // 若狀態為CCF,為案件重送
        if (StatusCode.CASE_CLOSED_FAIL.equals(statusCode)) {
            return CaseInfo.builder()
                    .caseNo(latestCase.getCaseNo())
                    .resend(true)
                    .build();
        }
        
        // 若狀態為HT,同新增案件
        if (StatusCode.HOLD_TEMP.equals(statusCode)) {
            return CaseInfo.builder()
                    .caseNo(null)
                    .resend(false)  
                    .build();
        }

        // 其他狀態不允許新增
        throw new BusinessException(
            ErrorCode.DUPLICATE_DATA,
            "資料重複無法新增"
        );
    }

    /**
     * 檢查被保險人是否在抵繳名單中
     *
     * <p>根據被保險人身分證字號和租戶代碼檢查是否在抵繳名單中。
     * 此檢查僅適用於新案件，重送案件不需要檢查。
     *
     * @param insuredId 被保險人身分證字號
     * @param ctTenantId 國泰人壽租戶代碼
     * @throws BusinessException 當被保險人不在抵繳名單中時，拋出代碼為 E001 的異常
     */
    private void checkAllowlist(String insuredId, String ctTenantId) {
        boolean exists = allowlistRepository.existsByInsuredIdAndCtTenantId(insuredId, ctTenantId);
        if (!exists) {
            throw new BusinessException(
                ErrorCode.NOT_IN_ALLOWLIST,
                "資料不存在無法修改"
            );
        }
    }
} 