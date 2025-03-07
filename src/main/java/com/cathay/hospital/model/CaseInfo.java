package com.cathay.hospital.model;

/**
 * 案件資訊模型類
 *
 * <p>用於封裝案件查詢結果，包含案件編號和是否為重送案件的標記。
 * 主要用於案件新增前的檢核流程，判斷案件是否可以新增或重送。
 *
 * <p>案件狀態判斷邏輯：
 * <ul>
 *   <li>無案件編號且非重送：新案件</li>
 *   <li>有案件編號且為重送：可重送案件</li>
 *   <li>其他狀態：不可新增</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * CaseInfo info = CaseInfo.builder()
 *     .caseNo("CASE001")
 *     .resend(true)
 *     .build();
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
public class CaseInfo {
    /** 案件編號 */
    private String caseNo;
    
    /** 是否為重送案件 */
    private boolean resend;

    private CaseInfo(Builder builder) {
        this.caseNo = builder.caseNo;
        this.resend = builder.resend;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String caseNo;
        private boolean resend;

        public Builder caseNo(String caseNo) {
            this.caseNo = caseNo;
            return this;
        }

        public Builder resend(boolean resend) {
            this.resend = resend;
            return this;
        }

        public CaseInfo build() {
            return new CaseInfo(this);
        }
    }

    // Getters
    public String getCaseNo() { return caseNo; }
    public boolean isResend() { return resend; }
} 