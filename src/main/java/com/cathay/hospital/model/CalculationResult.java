package com.cathay.hospital.model;

/**
 * 試算結果模型類
 *
 * <p>用於封裝試算服務的回傳結果，包含試算金額和試算原因。
 * 使用 Builder 模式構建物件，確保資料的完整性和不可變性。
 *
 * <p>使用範例:
 * <pre>
 * CalculationResult result = CalculationResult.builder()
 *     .calculatedAmount("1000")
 *     .calReason("正常試算")
 *     .build();
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
public class CalculationResult {
    /** 試算金額 */
    private String calculatedAmount;
    
    /** 試算原因 */
    private String calReason;

    private CalculationResult(Builder builder) {
        this.calculatedAmount = builder.calculatedAmount;
        this.calReason = builder.calReason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String calculatedAmount;
        private String calReason;

        public Builder calculatedAmount(String calculatedAmount) {
            this.calculatedAmount = calculatedAmount;
            return this;
        }

        public Builder calReason(String calReason) {
            this.calReason = calReason;
            return this;
        }

        public CalculationResult build() {
            return new CalculationResult(this);
        }
    }

    // Getters
    public String getCalculatedAmount() { return calculatedAmount; }
    public String getCalReason() { return calReason; }
} 