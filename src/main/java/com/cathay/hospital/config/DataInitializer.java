package com.cathay.hospital.config;

import com.cathay.hospital.entity.OffsetAllowlist;
import com.cathay.hospital.entity.OffsetCase;
import com.cathay.hospital.repository.OffsetAllowlistRepository;
import com.cathay.hospital.repository.OffsetCaseRepository;
import com.cathay.hospital.constant.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 資料初始化配置類
 *
 * <p>負責在應用程式啟動時初始化必要的測試資料。
 * 主要用於開發和測試環境，初始化抵繳名單和案件資料。
 *
 * <p>初始化資料包括：
 * <ul>
 *   <li>抵繳名單測試資料</li>
 *   <li>案件狀態測試資料</li>
 * </ul>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private OffsetAllowlistRepository allowlistRepository;

    @Autowired
    private OffsetCaseRepository caseRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 初始化測試資料
     *
     * <p>在應用程式啟動時執行，新增測試用的抵繳名單和案件資料。
     */
    @PostConstruct
    public void initData() {
        try {
            // 檢查是否已存在
            if (!allowlistRepository.existsByCtTenantIdAndInsuredId("ct-03374707-ytzw8", "A123456789")) {
                // 初始化抵繳名單
                OffsetAllowlist allowlist = new OffsetAllowlist();
                allowlist.setInsuredId("A123456789");
                allowlist.setInsuredName("測試姓名");
                allowlist.setCtTenantId("ct-03374707-ytzw8");
                allowlist.setUpdateId("SYSTEM");
                allowlist.setUpdateTime(LocalDateTime.now());
                allowlistRepository.save(allowlist);
                log.info("Successfully initialized allowlist data");
            }

            // 檢查案件是否已存在
            if (!caseRepository.existsById("CASE001")) {
                // 初始化案件資料
                OffsetCase testCase = new OffsetCase();
                testCase.setCaseNo("CASE001");
                testCase.setAdmissionNo("ADM002");
                testCase.setStatusCode(StatusCode.CCF.getCode());
                testCase.setCtTenantId("ct-03374707-ytzw8");
                testCase.setUpdateId("SYSTEM");
                testCase.setUpdateTime(LocalDateTime.now());
                testCase.setCaseDate(LocalDateTime.now());
                testCase.setCaseType("O");
                testCase.setOrganizationId("ORG001");
                testCase.setUpdateTenant("ct-03374707-ytzw8");
                testCase.setInsuredId("A123456789");
                testCase.setInsuredName("測試姓名");
                testCase.setCharNo("CHAR001");
                caseRepository.save(testCase);
                log.info("Successfully initialized case data");
            }
        } catch (Exception e) {
            log.error("Error initializing data", e);
            // 不要拋出異常，讓應用程式可以繼續啟動
        }
    }

    @Override
    public void run(String... args) {
        // 移除重複的初始化代碼，因為已經在 initData() 中處理了
        log.info("DataInitializer.run() completed");
    }

    @PostConstruct
    public void init() {
        try {
            // 检查序列是否存在
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
                log.warn("Sequence 'offset_case_seq' does not exist, creating it...");
                String createSql = """
                    CREATE SEQUENCE IF NOT EXISTS public.offset_case_seq
                        INCREMENT 1
                        START 1
                        MINVALUE 1
                        MAXVALUE 9999999999
                        CACHE 1
                """;
                jdbcTemplate.execute(createSql);
                log.info("Sequence created successfully");
            }
        } catch (Exception e) {
            log.error("Error initializing sequence:", e);
        }
    }

    @PostConstruct
    public void checkDatabaseStructure() {
        try {
            // 檢查表是否存在
            String checkTableSql = """
                SELECT EXISTS (
                    SELECT FROM information_schema.tables 
                    WHERE table_schema = 'public' 
                    AND table_name = 'offset_case'
                )
            """;
            Boolean tableExists = jdbcTemplate.queryForObject(checkTableSql, Boolean.class);
            log.info("offset_case table exists: {}", tableExists);

            if (Boolean.TRUE.equals(tableExists)) {
                // 檢查表結構
                List<Map<String, Object>> columns = jdbcTemplate.queryForList("""
                    SELECT column_name, data_type
                    FROM information_schema.columns
                    WHERE table_schema = 'public' 
                    AND table_name = 'offset_case'
                """);
                log.info("Table structure: {}", columns);
            }
        } catch (Exception e) {
            log.error("Error checking database structure", e);
        }
    }

    @PostConstruct
    public void checkDatabaseAccess() {
        try {
            // 檢查表存取權限
            String sql = """
                SELECT grantee, privilege_type 
                FROM information_schema.table_privileges 
                WHERE table_schema = 'public' 
                AND table_name = 'offset_case'
            """;
            List<Map<String, Object>> privileges = jdbcTemplate.queryForList(sql);
            log.info("Table privileges: {}", privileges);
            
            // 嘗試插入測試數據
            String testSql = """
                INSERT INTO public.offset_case (
                    case_no, admission_no, status_code, ct_tenant_id, 
                    update_id, update_time, case_date, case_type,
                    update_tenant
                ) VALUES (
                    'TEST_' || NOW()::TEXT, 'TEST_ADM', 'INI', 'TEST', 
                    'SYSTEM', NOW(), NOW(), 'O', 'TEST'
                )
            """;
            jdbcTemplate.execute(testSql);
            log.info("Test insert successful");
            
            // 清理測試數據
            String cleanupSql = """
                DELETE FROM public.offset_case 
                WHERE case_no LIKE 'TEST_%'
            """;
            jdbcTemplate.execute(cleanupSql);
            log.info("Test data cleaned up");
            
        } catch (Exception e) {
            log.error("Database access check failed", e);
            // 不要拋出異常，讓應用程式可以繼續啟動
        }
    }
} 