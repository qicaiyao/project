package com.bank.demo.goldtrade.exception;

/** 业务异常：携带银行风格错误码 */
public class BizException extends RuntimeException {

    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
