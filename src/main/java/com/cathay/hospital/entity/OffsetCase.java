package com.cathay.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
@Table(name = "offset_case")
public class OffsetCase {
    /** 案件編號 (主鍵) */
    @Id
    @Column(name = "CASE_NO")
    private String caseNo;
    
    /** 案件日期 */
    @Column(name = "CASE_DATE")
    private LocalDateTime caseDate;
    
    /** 案件類型 */
    @Column(name = "CASE_TYPE")
    private String caseType;
    
    /** 機構代碼 */
    @Column(name = "ORGANIZATION_ID")
    private String organizationId;
    
    /** 國泰人壽租戶代碼 */
    @Column(name = "CT_TENANT_ID") 
    private String ctTenantId;
    
    @Column(name = "INSURED_NAME")
    private String insuredName;
    
    @Column(name = "INSURED_ID")
    private String insuredId;
    
    @Column(name = "CHAR_NO")
    private String charNo;
    
    @Column(name = "ADMISSION_NO")
    private String admissionNo;
    
    @Column(name = "ADMISSION_DATE")
    private LocalDateTime admissionDate;
    
    @Column(name = "SEND_DATE")
    private LocalDateTime sendDate;
    
    @Column(name = "CALCULATED_AMOUNT")
    private String calculatedAmount;
    
    @Column(name = "AUTH_AGREEMENT")
    private String authAgreement;
    
    @Column(name = "STATUS_CODE")
    private String statusCode;
    
    @Column(name = "UPDATE_ID")
    private String updateId;
    
    @Column(name = "UPDATE_TENANT")
    private String updateTenant;
    
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    // Getters and Setters
    public String getCaseNo() { return caseNo; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    
    public LocalDateTime getCaseDate() { return caseDate; }
    public void setCaseDate(LocalDateTime caseDate) { this.caseDate = caseDate; }
    
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    
    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }
    
    public String getCtTenantId() { return ctTenantId; }
    public void setCtTenantId(String ctTenantId) { this.ctTenantId = ctTenantId; }
    
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    
    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }
    
    public String getInsuredName() { return insuredName; }
    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }
    
    public String getInsuredId() { return insuredId; }
    public void setInsuredId(String insuredId) { this.insuredId = insuredId; }
    
    public String getCharNo() { return charNo; }
    public void setCharNo(String charNo) { this.charNo = charNo; }
    
    public LocalDateTime getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDateTime admissionDate) { this.admissionDate = admissionDate; }
    
    public LocalDateTime getSendDate() { return sendDate; }
    public void setSendDate(LocalDateTime sendDate) { this.sendDate = sendDate; }
    
    public String getCalculatedAmount() { return calculatedAmount; }
    public void setCalculatedAmount(String calculatedAmount) { this.calculatedAmount = calculatedAmount; }
    
    public String getAuthAgreement() { return authAgreement; }
    public void setAuthAgreement(String authAgreement) { this.authAgreement = authAgreement; }
    
    public String getUpdateId() { return updateId; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }
    
    public String getUpdateTenant() { return updateTenant; }
    public void setUpdateTenant(String updateTenant) { this.updateTenant = updateTenant; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
} 