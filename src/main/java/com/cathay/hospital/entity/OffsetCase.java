package com.cathay.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;

/**
 * 抵繳案件實體類
 *
 * <p>對應資料庫中的 offset_case 表，用於儲存抵繳案件的相關資訊。
 * 包含案件基本資料、被保險人資訊、住院資訊、試算結果等。
 *
 * <p>案件狀態說明：
 * <ul>
 *   <li>CCF (CASE_CLOSED_FAIL) - 案件結案失敗，允許重送</li>
 *   <li>HT (HOLD_TEMP) - 暫存狀態，視為新案件</li>
 *   <li>其他狀態 - 不允許新增或重送</li>
 * </ul>
 *
 * <p>資料庫欄位映射：
 * <pre>
 * CASE_NO         - 案件編號 (PK)
 * CASE_DATE       - 案件日期
 * CASE_TYPE       - 案件類型
 * ORGANIZATION_ID - 機構代碼
 * CT_TENANT_ID    - 國泰人壽租戶代碼
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Entity
@Table(name = "offset_case", schema = "public")
@Data
public class OffsetCase {
    /** 案件編號 (主鍵) */
    @Id
    @Column(name = "case_no")
    private String caseNo;
    
    /** 案件日期 */
    @Column(name = "CASE_DATE")
    private LocalDateTime caseDate;
    
    /** 案件類型 */
    @Column(name = "CASE_TYPE")
    private String caseType;
    
    /** 機構代碼 */
    @Column(name = "organization_id")
    private String organizationId;
    
    /** 國泰人壽租戶代碼 */
    @Column(name = "ct_tenant_id")
    private String ctTenantId;
    
    @Column(name = "insured_name")
    private String insuredName;
    
    @Column(name = "insured_id")
    private String insuredId;
    
    @Column(name = "char_no")
    private String charNo;
    
    @Column(name = "admission_no")
    private String admissionNo;
    
    @Column(name = "admission_date")
    private LocalDate admissionDate;
    
    @Column(name = "SEND_DATE")
    private LocalDateTime sendDate;
    
    @Column(name = "CALCULATED_AMOUNT")
    private String calculatedAmount;
    
    @Column(name = "AUTH_AGREEMENT")
    private String authAgreement;
    
    @Column(name = "status_code")
    private String statusCode;
    
    @Column(name = "update_id")
    private String updateId;
    
    @Column(name = "UPDATE_TENANT")
    private String updateTenant;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // Getters
    public String getCaseNo() { return caseNo; }
    public LocalDateTime getCaseDate() { return caseDate; }
    public String getCaseType() { return caseType; }
    public String getOrganizationId() { return organizationId; }
    public String getCtTenantId() { return ctTenantId; }
    public String getInsuredName() { return insuredName; }
    public String getInsuredId() { return insuredId; }
    public String getCharNo() { return charNo; }
    public String getAdmissionNo() { return admissionNo; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public LocalDateTime getSendDate() { return sendDate; }
    public String getCalculatedAmount() { return calculatedAmount; }
    public String getAuthAgreement() { return authAgreement; }
    public String getStatusCode() { return statusCode; }
    public String getUpdateId() { return updateId; }
    public String getUpdateTenant() { return updateTenant; }
    public LocalDateTime getUpdateTime() { return updateTime; }

    // Setters
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    public void setCaseDate(LocalDateTime caseDate) { this.caseDate = caseDate; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }
    public void setCtTenantId(String ctTenantId) { this.ctTenantId = ctTenantId; }
    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }
    public void setInsuredId(String insuredId) { this.insuredId = insuredId; }
    public void setCharNo(String charNo) { this.charNo = charNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
    public void setSendDate(LocalDateTime sendDate) { this.sendDate = sendDate; }
    public void setCalculatedAmount(String calculatedAmount) { this.calculatedAmount = calculatedAmount; }
    public void setAuthAgreement(String authAgreement) { this.authAgreement = authAgreement; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }
    public void setUpdateTenant(String updateTenant) { this.updateTenant = updateTenant; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
} 