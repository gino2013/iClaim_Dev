package com.cathay.hospital.service;

import com.cathay.hospital.constant.ErrorCode;
import com.cathay.hospital.constant.StatusCode;
import com.cathay.hospital.entity.OffsetCase;
import com.cathay.hospital.exception.BusinessException;
import com.cathay.hospital.model.CalculationResult;
import com.cathay.hospital.model.OffsetCaseRequest;
import com.cathay.hospital.repository.OffsetCaseRepository;
import com.cathay.hospital.service.external.CalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Qualifier;
import java.util.Optional;
import jakarta.persistence.EntityManager;

import java.util.Arrays;

import org.springframework.core.env.Environment;

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
@Transactional
public class OffsetCaseService {
    private static final Logger log = LoggerFactory.getLogger(OffsetCaseService.class);

    private final OffsetCaseRepository caseRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;
    private final CalculationService calculationService;

    public OffsetCaseService(
            OffsetCaseRepository caseRepository,
            JdbcTemplate jdbcTemplate,
            EntityManager entityManager,
            @Qualifier("calculationServiceImpl") CalculationService calculationService) {
        this.caseRepository = caseRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
        this.calculationService = calculationService;
    }

    @Value("${app.environment}")
    private String appEnvironment;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void testConnection() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            log.info("Successfully connected to database: {}", result);
        } catch (Exception e) {
            log.error("Failed to connect to database", e);
        }
    }

    @PostConstruct
    public void checkConfiguration() {
        log.info("Datasource URL from configuration: {}", datasourceUrl);
        log.info("App environment: {}", appEnvironment);
        log.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
    }

    /**
     * 處理抵繳案件的主要方法
     *
     * @param headers HTTP 請求標頭
     * @param request 案件請求資料
     * @return 試算結果
     * @throws BusinessException 當業務邏輯檢核失敗時
     */
    @Transactional(rollbackFor = Exception.class)
    public CalculationResult processCase(Map<String, String> headers, OffsetCaseRequest request) {
        try {
            // 检查数据库状态
            checkDatabaseStatus();
            
            // 1. 检查必要字段
            validateRequest(request);

            // 2. 获取租户代码
            String tenantId = headers.get("TENANT_ID");
            String ctTenantId = getTenantId(tenantId);

            // 3. 检查案件是否存在
            checkCaseExists(request.getAdmissionNo());

            // 4. 检查抵缴名单
            checkAllowlist(ctTenantId, request.getInsuredId());

            // 5. 生成案件编号
            String caseNo = generateCaseNo();

            // 6. 处理文档
            if (request.getDocument() != null) {
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(request.getDocument());
                    String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
                    log.debug("Decoded document: {}", decodedString);
                } catch (IllegalArgumentException e) {
                    log.error("Failed to decode document", e);
                    throw new BusinessException(ErrorCode.INVALID_DOCUMENT_FORMAT, "文档格式无效");
                }
            }

            // 7. 保存案件並立即刷新
            saveCase(caseNo, ctTenantId, request);
            entityManager.flush();

            // 8. 调用试算服务
            CalculationResult calculationResult = calculationService.calculate(caseNo, request);
            
            // 写入试算历程
            insertCalculationHistory(
                caseNo, 
                1, 
                calculationResult.getCalculatedAmount(), 
                calculationResult.getCalculationReason(),
                request.getUpdateId(), 
                ctTenantId
            );

            return calculationResult;

        } catch (BusinessException e) {
            log.error("Business error occurred while processing case", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing case", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误", e);
        }
    }

    private void validateRequest(OffsetCaseRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELDS, "必填字段缺失");
        }

        // 添加详细日志
        log.info("Validating request: {}", request);
        log.info("organizationId: [{}]", request.getOrganizationId());
        log.info("insuredName: [{}]", request.getInsuredName());
        log.info("insuredId: [{}]", request.getInsuredId());
        log.info("charNo: [{}]", request.getCharNo());
        log.info("admissionNo: [{}]", request.getAdmissionNo());
        log.info("admissionDate: [{}]", request.getAdmissionDate());
        log.info("updateId: [{}]", request.getUpdateId());

        List<String> missingFields = new ArrayList<>();

        if (!StringUtils.hasText(request.getOrganizationId())) {
            missingFields.add("organizationId");
        }
        if (!StringUtils.hasText(request.getInsuredName())) {
            missingFields.add("insuredName");
        }
        if (!StringUtils.hasText(request.getInsuredId())) {
            missingFields.add("insuredId");
        }
        if (!StringUtils.hasText(request.getCharNo())) {
            missingFields.add("charNo");
        }
        if (!StringUtils.hasText(request.getAdmissionNo())) {
            missingFields.add("admissionNo");
        }
        if (!StringUtils.hasText(request.getAdmissionDate())) {
            missingFields.add("admissionDate");
        }
        if (!StringUtils.hasText(request.getUpdateId())) {
            missingFields.add("updateId");
        }

        if (!missingFields.isEmpty()) {
            log.error("Missing required fields: {}", missingFields);
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELDS, 
                String.format("必填字段缺失: %s", String.join(", ", missingFields)));
        }
    }

    /**
     * 獲取當前環境設定
     *
     * @return 環境設定值
     */
    private String getEnvironment() {
        return appEnvironment;
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
    private String getTenantId(String tenantId) {
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
     * 檢查被保險人是否在抵繳名單中
     *
     * <p>根據被保險人身分證字號和租戶代碼檢查是否在抵繳名單中。
     * 此檢查僅適用於新案件，重送案件不需要檢查。
     *
     * @param insuredId 被保險人身分證字號
     * @param ctTenantId 國泰人壽租戶代碼
     * @throws BusinessException 當被保險人不在抵繳名單中時，拋出代碼為 E001 的異常
     */
    private void checkAllowlist(String ctTenantId, String insuredId) {
        String sql = """
            SELECT COUNT(*) 
            FROM public.offset_allowlist 
            WHERE insured_id = ? 
            AND ct_tenant_id = ?
            AND update_time = (
                SELECT MAX(update_time) 
                FROM public.offset_allowlist 
                WHERE insured_id = ? 
                AND ct_tenant_id = ?
            )
        """;

        Integer count = jdbcTemplate.queryForObject(
            sql, 
            Integer.class,
            insuredId, ctTenantId, insuredId, ctTenantId
        );

        if (count == null || count == 0) {
            log.error("Insured {} from tenant {} not in allowlist", insuredId, ctTenantId);
            throw new BusinessException(
                ErrorCode.NOT_IN_ALLOWLIST,
                "資料不存在無法修改"
            );
        }

        log.info("Allowlist check passed for insured {} from tenant {}", insuredId, ctTenantId);
    }

    @Transactional
    public Map<String, Object> addDocCal(Map<String, Object> input) {
        try {
            // 1.检查必要字段
            validateRequiredFieldsForAddDocCal(input);

            // 2. DB资料检查
            String xCtId = getTenantIdForEnv();
            
            // 2.1 检查抵繳名单
            String insuredId = input.get("INSURED_ID").toString();
            String tenantId = input.get("TENANT_ID").toString();
            checkAllowlist(tenantId, insuredId);

            // 2.2 检查案件是否已存在
            String sql = "SELECT case_no, status_code, update_time FROM xDbSchema.offset_case " +
                        "WHERE admission_no = ? AND ct_tenant_id = ? " +
                        "ORDER BY update_time DESC LIMIT 1";
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, 
                input.get("ADMISSION_NO"), xCtId);

            if (!results.isEmpty()) {
                Map<String, Object> existingCase = results.get(0);
                String statusCode = (String) existingCase.get("status_code");
                
                if ("CCF".equals(statusCode)) {
                    throw new RuntimeException("0006:资料重复无法新增");
                } else if ("HT".equals(statusCode)) {
                    throw new RuntimeException("0006:资料重复无法新增");
                } else {
                    throw new RuntimeException("0006:资料重复无法新增");
                }
            }

            // 2.3 产生传输文件次数
            int xNum;
            xNum = 1;

            // 7.2 更新案件主档
            sql = "INSERT INTO xDbSchema.offset_case (" +
                  "case_no, case_date, case_type, organization_id, ct_tenant_id, " +
                  "insured_name, insured_id, char_no, admission_no, admission_date, " +
                  "send_date, calculated_amount, auth_agreement, status_code, " +
                  "update_id, update_tenant, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                input.get("case_no"),
                LocalDate.now(),
                "O",
                input.get("ORGANIZATION_ID"),
                xCtId,
                input.get("INSURED_NAME"),
                input.get("INSURED_ID"), 
                input.get("CHAR_NO"),
                input.get("ADMISSION_NO"),
                input.get("ADMISSION_DATE"),
                Timestamp.valueOf(LocalDateTime.now()),
                0, // xCalAmt 待计算
                input.get("AUTH_AGREEMENT"),
                "CCF",
                input.get("UPDATE_ID"),
                input.get("TENANT_ID"),
                Timestamp.valueOf(LocalDateTime.now())
            );
            
            // 写入案件日志
            insertCaseLog(input.get("case_no").toString(), "ADD");

            // 处理文档
            List<Map<String, String>> patternList = processDocuments(input);
            
            // 构建试算请求
            OffsetCaseRequest request = new OffsetCaseRequest();
            request.setDocuments(patternList);
            // 设置其他必要字段...

            // 调用试算服务
            CalculationResult calculationResult = calculationService.calculate(
                input.get("case_no").toString(),
                request
            );
            
            // 写入试算历程
            insertCalculationHistory(
                input.get("case_no").toString(), 
                xNum, 
                calculationResult.getCalculatedAmount(), 
                calculationResult.getCalculationReason(),
                input.get("UPDATE_ID").toString(), 
                input.get("TENANT_ID").toString()
            );

            // 返回结果
            return Map.of(
                "RETURN_CODE", "0000",
                "RETURN_DESC", "执行成功",
                "RETURN_DATA", Map.of(
                    "CALCULATED_AMOUNT", calculationResult.getCalculatedAmount(),
                    "CAL_REASON", calculationResult.getCalculationReason()
                )
            );

        } catch (Exception e) {
            log.error("Error processing case", e);
            return Map.of(
                "RETURN_CODE", e.getMessage().split(":")[0],
                "RETURN_DESC", e.getMessage().split(":")[1],
                "RETURN_DATA", Map.of()
            );
        }
    }

    /**
     * 检查案件新增必要字段
     */
    private void validateRequiredFieldsForAddDocCal(Map<String, Object> input) {
        String[] requiredFields = {
            "TXNSEQ", "TENANT_ID", "ORGANIZATION_ID", "INSURED_NAME",
            "INSURED_ID", "CHAR_NO", "ADMISSION_NO", "ADMISSION_DATE", "UPDATE_ID"
        };

        List<String> missingFields = new ArrayList<>();
        for (String field : requiredFields) {
            if (input.get(field) == null || input.get(field).toString().trim().isEmpty()) {
                throw new RuntimeException("0001:输入栏位缺漏或格式有误");
//                missingFields.add(field);
            }
        }

//        if (!missingFields.isEmpty()) {
//            log.error("Missing required fields: {}", missingFields);
//            throw new RuntimeException("0001:输入栏位缺漏或格式有误");
//        }
    }

    /**
     * 获取环境对应的国泰人寿租户代码
     */
    private String getTenantIdForEnv() {
        String currentEnv = getEnvironment();
        if (currentEnv == null) {
            log.error("Environment is null");
            throw new RuntimeException("A001:GIP查无资料");
        }
        
        currentEnv = currentEnv.trim().toUpperCase();
        log.info("Current environment: {}", currentEnv);
        
        switch (currentEnv) {
            case "UT":
                return "ct-03374707-ytzw8";
            case "UAT":
                return "ct-03374707-s7jbs";
            default:
                log.error("Invalid environment: {}", currentEnv);
                throw new RuntimeException("A001:GIP查无资料");
        }
    }

    /**
     * 写入案件日志
     */
    private void insertCaseLog(String caseNo, String logType) {
        String sql = "INSERT INTO xDbSchema.offset_case_log (" +
            "log_id, log_type, log_time, case_no, case_date, case_type, " +
            "organization_id, ct_tenant_id, insured_name, insured_id, " +
            "char_no, admission_no, admission_date, status_code, " +
            "calculated_amount, act_offset_amount, reject_reason, " +
            "auth_agreement, offset_date, close_date, send_date, " +
            "pay_date, update_id, update_tenant, update_time) " +
            "SELECT nextval('xDbSchema.offset_case_log_seq'), ?, CURRENT_TIMESTAMP, " +
            "case_no, case_date, case_type, organization_id, ct_tenant_id, " +
            "insured_name, insured_id, char_no, admission_no, admission_date, " +
            "status_code, calculated_amount, act_offset_amount, reject_reason, " +
            "auth_agreement, offset_date, close_date, send_date, pay_date, " +
            "update_id, update_tenant, update_time " +
            "FROM xDbSchema.offset_case WHERE case_no = ?";

        jdbcTemplate.update(sql, logType, caseNo);
    }

    /**
     * 写入试算历程
     */
    private void insertCalculationHistory(String caseNo, int docTypeNum, BigDecimal calAmt, String calReason, String updateId, String tenantId) {
        String sql = "INSERT INTO xDbSchema.offset_case_cal (" +
            "case_no, doc_type_num, calculated_amount, cal_reason, " +
            "update_id, update_tenant, update_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        jdbcTemplate.update(sql,
            caseNo,
            docTypeNum,
            calAmt,
            calReason,
            updateId,
            tenantId
        );
    }

    /**
     * 获取理赔模板设定
     * @param caseType 案件类型
     * @return 模板设定列表
     */
    private List<Map<String, Object>> getDocPatterns(String caseType) {
        String sql = """
            SELECT 
                pattern_id,
                pattern_name,
                paper_seq,
                required_flag,
                status_code
            FROM public.case_doc_pattern 
            WHERE case_type = ?
            AND status_code = '1'
            ORDER BY pattern_id
        """;

        return jdbcTemplate.queryForList(sql, caseType);
    }

    /**
     * 处理文档模板
     * @param input 输入参数
     * @return 处理后的文档列表
     */
    private List<Map<String, String>> processDocuments(Map<String, Object> input) {
        // 1. 获取理赔模板设定
        List<Map<String, Object>> patterns = getDocPatterns("O");  // O 表示抵繳案件
        
        // 2. 获取住院号
        String admissionNo = input.get("ADMISSION_NO").toString();
        
        // 3. 获取所有必要文件的序号
        List<String> requiredPaperSeqs = patterns.stream()
            .filter(p -> "Y".equals(p.get("required_flag")))
            .map(p -> p.get("paper_seq").toString())
            .toList();
            
        // 4. 查询 FHIR bundle 序号
        List<String> bundleSeqs = getBundleSeqs(admissionNo, requiredPaperSeqs);
        if (bundleSeqs.isEmpty()) {
            log.error("No FHIR bundles found for admission: {}", admissionNo);
            throw new RuntimeException("0003:必要文件不完整");
        }
        
        // 5. 查询 FHIR 资源路径
        List<String> resourcePaths = getFhirPaths(bundleSeqs);
        if (resourcePaths.isEmpty()) {
            log.error("No FHIR resources found for bundles: {}", bundleSeqs);
            throw new RuntimeException("0004:FHIR资源不存在");
        }
        
        // 6. 构建返回的文档列表
        List<Map<String, String>> documents = new ArrayList<>();
        final int size = bundleSeqs.size();
        for (int i = 0; i < size; i++) {
            final int index = i;  // 创建一个 effectively final 的变量
            Map<String, String> doc = new HashMap<>();
            doc.put("xPaperSeq", requiredPaperSeqs.get(index));
            doc.put("xBatchId", bundleSeqs.get(index));
            doc.put("xPaperName", patterns.stream()
                .filter(p -> p.get("paper_seq").toString().equals(requiredPaperSeqs.get(index)))
                .findFirst()
                .map(p -> p.get("pattern_name").toString())
                .orElse(""));
            doc.put("UPDATE_ID", input.get("UPDATE_ID").toString());
            documents.add(doc);
        }
        
        return documents;
    }

    /**
     * 查询 FHIR bundle 序号
     */
    private List<String> getBundleSeqs(String admissionNo, List<String> paperSeqs) {
        if (paperSeqs == null || paperSeqs.isEmpty()) {
            return new ArrayList<>();
        }

        String sql = """
            SELECT DISTINCT bundle_seq 
            FROM public.fhir_server_bundle 
            WHERE admission_no = ? 
            AND paper_seq IN (%s)
            AND status_code = '1'
            ORDER BY bundle_seq
        """;

        String inClause = String.join(",", Collections.nCopies(paperSeqs.size(), "?"));
        sql = String.format(sql, inClause);

        List<Object> params = new ArrayList<>();
        params.add(admissionNo);
        params.addAll(paperSeqs);

        return jdbcTemplate.queryForList(sql, String.class, params.toArray());
    }

    /**
     * 查询 FHIR 资源路径
     */
    private List<String> getFhirPaths(List<String> bundleSeqs) {
        if (bundleSeqs == null || bundleSeqs.isEmpty()) {
            return new ArrayList<>();
        }

        String sql = """
            SELECT DISTINCT resource_path 
            FROM public.fhir_server_resource 
            WHERE bundle_seq IN (%s)
            AND status_code = '1'
            ORDER BY resource_path
        """;

        String inClause = String.join(",", Collections.nCopies(bundleSeqs.size(), "?"));
        sql = String.format(sql, inClause);

        return jdbcTemplate.queryForList(sql, String.class, bundleSeqs.toArray());
    }

    /**
     * 检查案件是否存在
     */
    private void checkCaseExists(String admissionNo) {
        String sql = """
            SELECT COUNT(*) 
            FROM public.offset_case 
            WHERE admission_no = ?
        """;
        
        log.info("Checking if case exists for admission_no: {}", admissionNo);
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, admissionNo);
            log.info("Found {} existing cases with admission_no: {}", count, admissionNo);
            
            if (count != null && count > 0) {
                log.warn("Case already exists with admission_no: {}", admissionNo);
                throw new BusinessException("0003", "案件已存在");
            }
        } catch (Exception e) {
            log.error("Error checking case existence: {}", e.getMessage());
            log.error("SQL: {}", sql);
            log.error("Parameters: admission_no = {}", admissionNo);
            throw e;
        }
    }

    /**
     * 生成案件编号
     */
    private String generateCaseNo() {
        try {
            // 先尝试直接获取序列值
            String sql = "SELECT nextval('public.offset_case_seq')";
            log.debug("Executing SQL: {}", sql);
            
            Long seqNo = jdbcTemplate.queryForObject(sql, Long.class);
            log.info("Generated sequence number: {}", seqNo);
            
            if (seqNo == null) {
                log.error("Failed to generate sequence number - returned null");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "無法生成序列號");
            }
            
            String caseNo = String.format("OC%010d", seqNo);
            log.info("Generated case number: {}", caseNo);
            return caseNo;
            
        } catch (Exception e) {
            log.error("Error generating case number: {}", e.getMessage());
            log.error("Error details:", e);
            
            // 检查序列是否存在
            try {
                String checkSql = """
                    SELECT EXISTS (
                        SELECT 1 
                        FROM pg_sequences 
                        WHERE schemaname = 'public' 
                        AND sequencename = 'offset_case_seq'
                    )
                """;
                Boolean exists = jdbcTemplate.queryForObject(checkSql, Boolean.class);
                log.info("Sequence exists check result: {}", exists);
                
                if (Boolean.FALSE.equals(exists)) {
                    log.error("Sequence 'offset_case_seq' does not exist");
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "序列不存在");
                }
            } catch (Exception checkError) {
                log.error("Error checking sequence existence:", checkError);
            }
            
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成案件編號失敗");
        }
    }

    /**
     * 保存案件
     */
    private void saveCase(String caseNo, String ctTenantId, OffsetCaseRequest request) {
        log.info("Starting to save case - caseNo: {}, admissionNo: {}", caseNo, request.getAdmissionNo());
        
        try {
            OffsetCase offsetCase = new OffsetCase();
            offsetCase.setCaseNo(caseNo);
            offsetCase.setCtTenantId(ctTenantId);
            offsetCase.setAdmissionNo(request.getAdmissionNo());
            offsetCase.setInsuredId(request.getInsuredId());
            offsetCase.setInsuredName(request.getInsuredName());
            offsetCase.setOrganizationId(request.getOrganizationId());
            offsetCase.setCharNo(request.getCharNo());
            offsetCase.setAdmissionDate(LocalDate.parse(request.getAdmissionDate()));
            offsetCase.setUpdateId(request.getUpdateId());
            offsetCase.setUpdateTime(LocalDateTime.now());
            offsetCase.setUpdateTenant(ctTenantId);
            offsetCase.setStatusCode(StatusCode.INIT.getCode());
            offsetCase.setCaseDate(LocalDateTime.now());
            offsetCase.setCaseType("O");

            // 在保存前檢查所有必要欄位
            validateOffsetCase(offsetCase);
            
            log.info("Prepared case entity for saving: {}", offsetCase);
            
            // 使用 JdbcTemplate 直接插入
            String sql = """
                INSERT INTO public.offset_case (
                    case_no, ct_tenant_id, admission_no, insured_id, 
                    insured_name, organization_id, char_no, admission_date,
                    update_id, update_time, update_tenant, status_code, 
                    case_date, case_type
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            
            jdbcTemplate.update(sql,
                offsetCase.getCaseNo(),
                offsetCase.getCtTenantId(),
                offsetCase.getAdmissionNo(),
                offsetCase.getInsuredId(),
                offsetCase.getInsuredName(),
                offsetCase.getOrganizationId(),
                offsetCase.getCharNo(),
                offsetCase.getAdmissionDate(),
                offsetCase.getUpdateId(),
                offsetCase.getUpdateTime(),
                offsetCase.getUpdateTenant(),
                offsetCase.getStatusCode(),
                offsetCase.getCaseDate(),
                offsetCase.getCaseType()
            );
            
            log.info("Successfully inserted case using JDBC");
            
            // 驗證插入是否成功
            String checkSql = "SELECT COUNT(*) FROM public.offset_case WHERE case_no = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, caseNo);
            
            if (count == null || count == 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "案件保存失敗 - 資料未成功寫入");
            }
            
            log.info("Successfully verified case in database");
            
        } catch (Exception e) {
            log.error("Error saving case: ", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存案件失敗: " + e.getMessage());
        }
    }

    private void validateOffsetCase(OffsetCase offsetCase) {
        List<String> missingFields = new ArrayList<>();
        
        if (offsetCase.getCaseNo() == null) missingFields.add("caseNo");
        if (offsetCase.getCtTenantId() == null) missingFields.add("ctTenantId");
        if (offsetCase.getAdmissionNo() == null) missingFields.add("admissionNo");
        if (offsetCase.getStatusCode() == null) missingFields.add("statusCode");
        if (offsetCase.getUpdateId() == null) missingFields.add("updateId");
        if (offsetCase.getUpdateTime() == null) missingFields.add("updateTime");
        if (offsetCase.getUpdateTenant() == null) missingFields.add("updateTenant");
        
        if (!missingFields.isEmpty()) {
            String errorMsg = "必要欄位缺失: " + String.join(", ", missingFields);
            log.error(errorMsg);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMsg);
        }
    }

    /**
     * 检查数据库状态
     */
    private void checkDatabaseStatus() {
        try {
            // 检查数据库连接
            String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            log.info("Connected to database: {}", dbName);
            
            // 检查表是否存在
            String checkTableSql = """
                SELECT EXISTS (
                    SELECT 1 
                    FROM information_schema.tables 
                    WHERE table_schema = 'public' 
                    AND table_name = 'offset_case'
                )
            """;
            Boolean tableExists = jdbcTemplate.queryForObject(checkTableSql, Boolean.class);
            log.info("offset_case table exists: {}", tableExists);
            
            // 检查表中的记录数
            Integer recordCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM public.offset_case", Integer.class);
            log.info("Total records in offset_case table: {}", recordCount);
            
        } catch (Exception e) {
            log.error("Error checking database status: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库检查失败", e);
        }
    }
} 