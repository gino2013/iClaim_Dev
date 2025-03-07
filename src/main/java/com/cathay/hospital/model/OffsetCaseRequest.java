package com.cathay.hospital.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 案件新增請求模型類
 *
 * <p>用於封裝案件新增及試算的請求參數。包含所有必要的案件資訊，如被保險人資料、
 * 住院資訊等。支援透過 Builder 模式建立物件。
 *
 * <p>必要欄位包括：
 * <ul>
 *   <li>organizationId - 機構代碼</li>
 *   <li>insuredName - 被保險人姓名</li>
 *   <li>insuredId - 被保險人身分證字號</li>
 *   <li>charNo - 就醫序號</li>
 *   <li>admissionNo - 住院號</li>
 *   <li>admissionDate - 住院日期</li>
 *   <li>updateId - 更新人員</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * OffsetCaseRequest request = new OffsetCaseRequest();
 * request.setOrganizationId("ORG001");
 * request.setInsuredName("測試姓名");
 * // ... 設置其他必要欄位
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OffsetCaseRequest {
    /** 機構代碼 */
    private String organizationId;
    
    /** 被保險人姓名 */
    private String insuredName;
    
    /** 被保險人身分證字號 */
    private String insuredId;
    
    /** 就醫序號 */
    private String charNo;
    
    /** 住院號 */
    private String admissionNo;
    
    /** 住院日期 (YYYY-MM-DD) */
    private String admissionDate;
    
    /** 授權同意書 (Y/N) */
    // private String authAgreement;
    
    /** 更新人員 */
    private String updateId;

    private List<Map<String, String>> documents;
    private String document;  // Base64 encoded document
    private String policyNo;
    private BigDecimal offsetAmount;

    // Getter methods
    public String getOrganizationId() {
        return organizationId;
    }

    public String getCharNo() {
        return charNo;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public String getUpdateId() {
        return updateId;
    }

    public List<Map<String, String>> getDocuments() {
        return documents;
    }

    // Setter methods
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setCharNo(String charNo) {
        this.charNo = charNo;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public void setDocuments(List<Map<String, String>> documents) {
        this.documents = documents;
    }

    // 手动添加 getter/setter 方法
    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public void setInsuredId(String insuredId) {
        this.insuredId = insuredId;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public void setOffsetAmount(BigDecimal offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public String getInsuredId() {
        return insuredId;
    }

    public String getDocument() {
        return document;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public BigDecimal getOffsetAmount() {
        return offsetAmount;
    }
} 