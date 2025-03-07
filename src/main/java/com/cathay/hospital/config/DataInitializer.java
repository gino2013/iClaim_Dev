package com.cathay.hospital.config;

import com.cathay.hospital.entity.OffsetAllowlist;
import com.cathay.hospital.entity.OffsetCase;
import com.cathay.hospital.repository.OffsetAllowlistRepository;
import com.cathay.hospital.repository.OffsetCaseRepository;
import com.cathay.hospital.constant.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

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

    @Autowired
    private OffsetAllowlistRepository allowlistRepository;

    @Autowired
    private OffsetCaseRepository caseRepository;

    /**
     * 初始化測試資料
     *
     * <p>在應用程式啟動時執行，新增測試用的抵繳名單和案件資料。
     */
    @PostConstruct
    public void initData() {
        // 初始化抵繳名單
        OffsetAllowlist allowlist = new OffsetAllowlist();
        allowlist.setInsuredId("A123456789");
        allowlist.setInsuredName("測試姓名");
        allowlist.setCtTenantId("ct-03374707-ytzw8");
        allowlist.setUpdateId("SYSTEM");
        allowlist.setUpdateTime(LocalDateTime.now());
        allowlistRepository.save(allowlist);

        // 初始化案件資料
        OffsetCase testCase = new OffsetCase();
        testCase.setCaseNo("CASE001");
        testCase.setAdmissionNo("ADM002");
        testCase.setStatusCode(StatusCode.CASE_CLOSED_FAIL);
        testCase.setCtTenantId("ct-03374707-ytzw8");
        testCase.setUpdateId("SYSTEM");
        testCase.setUpdateTime(LocalDateTime.now());
        caseRepository.save(testCase);
    }

    @Override
    public void run(String... args) {
        // 添加測試用的抵繳名單數據
        OffsetAllowlist allowlist = new OffsetAllowlist();
        allowlist.setInsuredId("A123456789");
        allowlist.setCtTenantId("ct-03374707-ytzw8");
        allowlistRepository.save(allowlist);

        // 添加測試用的案件數據
        OffsetCase testCase = new OffsetCase();
        testCase.setCaseNo("TEST001");
        testCase.setAdmissionNo("ADM002");  // 使用不同的號碼以避免衝突
        testCase.setCtTenantId("ct-03374707-ytzw8");
        testCase.setStatusCode("CCF");
        caseRepository.save(testCase);
    }
} 