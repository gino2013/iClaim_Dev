package com.cathay.hospital.model;

import lombok.Getter;
import java.util.HashMap;

/**
 * API 響應封裝類
 *
 * <p>用於統一封裝 API 的響應格式，包含響應碼、響應描述和響應數據。
 * 支持成功和錯誤兩種響應類型的構建。
 *
 * <p>響應格式示例:
 * <pre>
 * {
 *   "returnCode": "0000",
 *   "returnDesc": "執行成功",
 *   "returnData": { ... }
 * }
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@Getter
public class ApiResponse {
    /** 響應碼 */
    private final String returnCode;
    
    /** 響應描述 */
    private final String returnDesc;
    
    /** 響應數據 */
    private final Object returnData;

    private ApiResponse(Builder builder) {
        this.returnCode = builder.returnCode;
        this.returnDesc = builder.returnDesc;
        this.returnData = builder.returnData;
    }

    public static ApiResponse success(Object data) {
        return defaultBuilder()
                .returnData(data)
                .build();
    }

    public static ApiResponse error(String code, String message) {
        return new Builder()
                .returnCode(code)
                .returnDesc(message)
                .returnData(new HashMap<>())
                .build();
    }

    private static Builder defaultBuilder() {
        return new Builder()
                .returnCode("0000")
                .returnDesc("執行成功");
    }

    public static class Builder {
        private String returnCode;
        private String returnDesc;
        private Object returnData;

        public Builder returnCode(String returnCode) {
            this.returnCode = returnCode;
            return this;
        }

        public Builder returnDesc(String returnDesc) {
            this.returnDesc = returnDesc;
            return this;
        }

        public Builder returnData(Object returnData) {
            this.returnData = returnData;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(this);
        }
    }
} 