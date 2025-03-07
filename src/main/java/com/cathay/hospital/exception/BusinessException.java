package com.cathay.hospital.exception;

import lombok.Getter;

/**
 * 業務邏輯異常類
 *
 * <p>用於封裝業務邏輯處理過程中的異常情況，包含錯誤代碼和錯誤訊息。
 * 主要用於以下場景：
 * <ul>
 *   <li>必要欄位缺漏或格式錯誤</li>
 *   <li>資料重複無法新增</li>
 *   <li>資料不存在無法修改</li>
 *   <li>GIP查無資料</li>
 * </ul>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Getter
public class BusinessException extends RuntimeException {
    /** 錯誤代碼 */
    private final String code;
    
    /** 錯誤訊息 */
    private final String message;

    /**
     * 建構子
     *
     * @param code 錯誤代碼
     * @param message 錯誤訊息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
} 