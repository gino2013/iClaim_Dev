package com.cathay.hospital.repository;

import com.cathay.hospital.entity.OffsetAllowlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 抵繳名單資料存取介面
 *
 * <p>提供抵繳名單相關的資料庫操作方法，主要用於檢核被保險人是否在抵繳名單中。
 * 繼承自 JpaRepository，提供基本的 CRUD 操作。
 *
 * <p>主要功能：
 * <ul>
 *   <li>檢查被保險人是否在抵繳名單中</li>
 *   <li>根據身分證字號和租戶代碼查詢名單</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * boolean isAllowed = repository.existsByInsuredIdAndCtTenantId(
 *     "A123456789", "ct-03374707-ytzw8");
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Repository
public interface OffsetAllowlistRepository extends JpaRepository<OffsetAllowlist, Long> {
    
    /**
     * 檢查指定被保險人是否在抵繳名單中
     *
     * @param insuredId 被保險人身分證字號
     * @param ctTenantId 國泰人壽租戶代碼
     * @return true 如果在名單中，否則返回 false
     */
    boolean existsByInsuredIdAndCtTenantId(String insuredId, String ctTenantId);
} 