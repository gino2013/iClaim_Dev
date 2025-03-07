package com.cathay.hospital.model;

import lombok.Data;

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
    private String authAgreement;
    
    /** 更新人員 */
    private String updateId;

    private byte[] document;

    public OffsetCaseRequest() {}

    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }
    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }
    public void setInsuredId(String insuredId) { this.insuredId = insuredId; }
    public void setCharNo(String charNo) { this.charNo = charNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }
    public void setAdmissionDate(String admissionDate) { this.admissionDate = admissionDate; }
    public void setAuthAgreement(String authAgreement) { this.authAgreement = authAgreement; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }
    public void setDocument(byte[] document) { this.document = document; }

    public String getOrganizationId() { return organizationId; }
    public String getInsuredName() { return insuredName; }
    public String getInsuredId() { return insuredId; }
    public String getCharNo() { return charNo; }
    public String getAdmissionNo() { return admissionNo; }
    public String getAdmissionDate() { return admissionDate; }
    public String getAuthAgreement() { return authAgreement; }
    public String getUpdateId() { return updateId; }
    public byte[] getDocument() { return document; }

    private OffsetCaseRequest(Builder builder) {
        this.organizationId = builder.organizationId;
        this.insuredName = builder.insuredName;
        this.insuredId = builder.insuredId;
        this.charNo = builder.charNo;
        this.admissionNo = builder.admissionNo;
        this.admissionDate = builder.admissionDate;
        this.authAgreement = builder.authAgreement;
        this.updateId = builder.updateId;
        this.document = builder.document;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String organizationId;
        private String insuredName;
        private String insuredId;
        private String charNo;
        private String admissionNo;
        private String admissionDate;
        private String authAgreement;
        private String updateId;
        private byte[] document;

        public Builder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder insuredName(String insuredName) {
            this.insuredName = insuredName;
            return this;
        }

        public Builder insuredId(String insuredId) {
            this.insuredId = insuredId;
            return this;
        }

        public Builder charNo(String charNo) {
            this.charNo = charNo;
            return this;
        }

        public Builder admissionNo(String admissionNo) {
            this.admissionNo = admissionNo;
            return this;
        }

        public Builder admissionDate(String admissionDate) {
            this.admissionDate = admissionDate;
            return this;
        }

        public Builder authAgreement(String authAgreement) {
            this.authAgreement = authAgreement;
            return this;
        }

        public Builder updateId(String updateId) {
            this.updateId = updateId;
            return this;
        }

        public Builder document(byte[] document) {
            this.document = document;
            return this;
        }

        public OffsetCaseRequest build() {
            return new OffsetCaseRequest(this);
        }
    }
} 