package com.cathay.hospital.constant;

/**
 * 錯誤代碼常量類
 *
 * <p>定義系統中使用的所有錯誤代碼，用於統一錯誤處理和響應。
 * 每個錯誤代碼對應一個特定的業務場景或錯誤情況。
 *
 * <p>錯誤代碼說明：
 * <ul>
 *   <li>0001 - 輸入欄位缺漏或格式有誤</li>
 *   <li>E001 - 資料不存在無法修改</li>
 *   <li>E002 - 資料重複無法新增</li>
 *   <li>A001 - GIP查無資料</li>
 * </ul>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
public class ErrorCode {
    /** 輸入欄位缺漏或格式有誤 */
    public static final String INVALID_INPUT = "0001";
    
    /** 資料不存在無法修改 */
    public static final String NOT_IN_ALLOWLIST = "E001";
    
    /** 資料重複無法新增 */
    public static final String DUPLICATE_DATA = "E002";
    
    /** GIP查無資料 */
    public static final String GIP_NOT_FOUND = "A001";
    
    public static final String NO_DATA = "0005";           // 查無資料
    public static final String DATA_CONVERT_ERROR = "0010";// 資料轉換失敗
    public static final String DOC_NOT_READY = "1024";     // 應備傳輸文件未備妥
    public static final String FHIR_NO_DATA = "A003";      // FHIR查無資料
    public static final String OTHER_API_ERROR = "A007";   // 呼叫其他服務API發生錯誤
    public static final String EXTERNAL_API_ERROR = "A009";// 呼叫外部API發生錯誤
    public static final String DB_ERROR = "9999";          // 發生資料庫錯誤
    
    private ErrorCode() {
        throw new IllegalStateException("Constant class");
    }
} 