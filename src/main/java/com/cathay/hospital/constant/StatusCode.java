package com.cathay.hospital.constant;

/**
 * 案件狀態代碼常量類
 *
 * <p>定義系統中使用的所有案件狀態代碼，用於標識案件在不同階段的處理狀態。
 * 每個狀態代碼對應一個特定的案件處理階段。
 *
 * <p>狀態代碼說明：
 * <ul>
 *   <li>CCF (CASE_CLOSED_FAIL) - 案件結案失敗，允許重送</li>
 *   <li>HT (HOLD_TEMP) - 暫存狀態，視為新案件</li>
 *   <li>CCS (CASE_CLOSED_SUCCESS) - 案件結案成功</li>
 *   <li>IP (IN_PROCESS) - 處理中</li>
 * </ul>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
public enum StatusCode {
    INIT("0"),      // 初始状态
    ACTIVE("1"),    // 有效
    INVALID("9"),    // 无效
    CCF("CCF"),      // 案件結案失敗，允許重送
    HT("HT"),        // 暫存狀態，視為新案件
    CCS("CCS"),      // 案件結案成功
    IP("IP");        // 處理中
    
    private final String code;
    
    StatusCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
} 