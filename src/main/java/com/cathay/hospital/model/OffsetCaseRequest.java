package com.cathay.hospital.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * 案件新增請求模型類
 * ...
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

    /** 更新人員 */
    private String updateId;

    /** 文件列表 */
    private List<Map<String, String>> documents;

    /** Base64 編碼的文件 */
    private String document;

    /** 保單號碼 */
    private String policyNo;

    /** 理賠金額 */
    private BigDecimal offsetAmount;
}