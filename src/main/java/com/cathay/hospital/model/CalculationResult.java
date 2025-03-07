package com.cathay.hospital.model;

import java.math.BigDecimal;

/**
 * 試算結果模型類
 *
 * <p>用於封裝試算服務的回傳結果，包含試算金額和試算原因。
 * 使用 Builder 模式構建物件，確保資料的完整性和不可變性。
 *
 * <p>使用範例:
 * <pre>
 * CalculationResult result = CalculationResult.builder()
 *     .calculatedAmount(new BigDecimal("1000"))
 *     .calculationReason("正常試算")
 *     .build();
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
public class CalculationResult {
    private String caseNo;                  // 案件編號
    private BigDecimal calculatedAmount;    // 試算金額
    private String calculationReason;       // 試算原因

    private CalculationResult(Builder builder) {
        this.caseNo = builder.caseNo;
        this.calculatedAmount = builder.calculatedAmount;
        this.calculationReason = builder.calculationReason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCaseNo() {
        return caseNo;
    }

    public BigDecimal getCalculatedAmount() {
        return calculatedAmount;
    }

    public String getCalculationReason() {
        return calculationReason;
    }

    public static class Builder {
        private String caseNo;
        private BigDecimal calculatedAmount;
        private String calculationReason;

        public Builder caseNo(String caseNo) {
            this.caseNo = caseNo;
            return this;
        }

        public Builder calculatedAmount(BigDecimal calculatedAmount) {
            this.calculatedAmount = calculatedAmount;
            return this;
        }

        public Builder calculationReason(String calculationReason) {
            this.calculationReason = calculationReason;
            return this;
        }

        public CalculationResult build() {
            return new CalculationResult(this);
        }
    }
} 