package com.bank.demo.goldtrade.exception;

import com.bank.demo.goldtrade.dto.TradeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：任何异常统一转换为 XML 报文返回，保证手机银行侧解析行为一致。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ResponseEntity<TradeResponse> handleBiz(BizException e) {
        log.warn("业务异常: {} - {}", e.getCode(), e.getMessage());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(resp(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TradeResponse> handleOther(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_XML)
                .body(resp("E9999", "系统异常，请稍后重试"));
    }

    private TradeResponse resp(String code, String msg) {
        TradeResponse r = new TradeResponse();
        r.setReturnCode(code);
        r.setReturnMsg(msg);
        return r;
    }
}
