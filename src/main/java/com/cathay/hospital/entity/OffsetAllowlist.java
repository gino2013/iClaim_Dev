package com.cathay.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 抵繳名單實體類
 *
 * <p>對應資料庫中的 offset_allowlist 表，用於儲存允許進行抵繳的被保險人名單。
 * 主要用於案件新增時的資格檢核。
 *
 * <p>資料庫欄位映射：
 * <pre>
 * ID            - 主鍵 (自動遞增)
 * INSURED_ID    - 被保險人身分證字號
 * INSURED_NAME  - 被保險人姓名
 * CT_TENANT_ID  - 國泰人壽租戶代碼
 * UPDATE_ID     - 更新人員
 * UPDATE_TIME   - 更新時間
 * </pre>
 *
 * <p>使用範例:
 * <pre>
 * OffsetAllowlist allowlist = new OffsetAllowlist();
 * allowlist.setInsuredId("A123456789");
 * allowlist.setInsuredName("測試姓名");
 * allowlist.setCtTenantId("ct-03374707-ytzw8");
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Entity
@Table(name = "offset_allowlist")
public class OffsetAllowlist {
    /** 主鍵 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 被保險人身分證字號 */
    @Column(name = "INSURED_ID")
    private String insuredId;
    
    /** 被保險人姓名 */
    @Column(name = "INSURED_NAME")
    private String insuredName;
    
    /** 國泰人壽租戶代碼 */
    @Column(name = "CT_TENANT_ID")
    private String ctTenantId;
    
    /** 更新人員 */
    @Column(name = "UPDATE_ID")
    private String updateId;
    
    /** 更新時間 */
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getInsuredId() { return insuredId; }
    public void setInsuredId(String insuredId) { this.insuredId = insuredId; }
    
    public String getInsuredName() { return insuredName; }
    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }
    
    public String getCtTenantId() { return ctTenantId; }
    public void setCtTenantId(String ctTenantId) { this.ctTenantId = ctTenantId; }
    
    public String getUpdateId() { return updateId; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
} 