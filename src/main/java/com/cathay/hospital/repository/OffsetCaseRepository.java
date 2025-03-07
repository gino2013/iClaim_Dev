package com.cathay.hospital.repository;

import com.cathay.hospital.entity.OffsetCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 抵繳案件資料存取介面
 *
 * <p>提供抵繳案件相關的資料庫操作方法，包括查詢、新增、更新等功能。
 * 繼承自 JpaRepository，提供基本的 CRUD 操作。
 *
 * <p>主要功能：
 * <ul>
 *   <li>根據住院號和租戶代碼查詢最新案件</li>
 *   <li>檢查案件是否存在</li>
 *   <li>新增案件</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * Optional<OffsetCase> case = repository.findLatestByAdmissionNoAndCtTenantId(
 *     "ADM001", "ct-03374707-ytzw8");
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Repository
public interface OffsetCaseRepository extends JpaRepository<OffsetCase, String> {
    
    /**
     * 根據住院號和租戶代碼查詢最新案件
     *
     * @param admissionNo 住院號
     * @param ctTenantId 國泰人壽租戶代碼
     * @return 最新的案件資訊
     */
    @Query("SELECT o FROM OffsetCase o WHERE o.admissionNo = :admissionNo AND o.ctTenantId = :ctTenantId ORDER BY o.updateTime DESC")
    Optional<OffsetCase> findLatestByAdmissionNoAndCtTenantId(String admissionNo, String ctTenantId);
    
    @Query("SELECT COUNT(c) FROM OffsetCase c WHERE CAST(c.caseDate AS date) = CURRENT_DATE")
    long countTodayCases();

    boolean existsById(String caseNo);
} 