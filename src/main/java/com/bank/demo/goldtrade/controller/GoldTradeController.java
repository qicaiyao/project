package com.bank.demo.goldtrade.controller;

import com.bank.demo.goldtrade.dto.TradeRequest;
import com.bank.demo.goldtrade.dto.TradeResponse;
import com.bank.demo.goldtrade.service.GoldTradeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 黄金交易统一入口（HTTP + XML）
 * <p>
 * 手机银行前置系统调用方式：
 * <pre>
 * POST /api/gold/service
 * Content-Type: application/xml
 * Accept: application/xml
 * </pre>
 */
@RestController
@RequestMapping("/api/gold")
public class GoldTradeController {

    private final GoldTradeService goldTradeService;

    public GoldTradeController(GoldTradeService goldTradeService) {
        this.goldTradeService = goldTradeService;
    }

    /**
     * 统一交易入口，按请求报文中的 txnCode 分发：
     * 1001 金价查询 / 1002 买入 / 1003 卖出 / 1004 持仓查询 / 1005 交易明细查询
     */
    @PostMapping(value = "/service",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = MediaType.APPLICATION_XML_VALUE)
    public TradeResponse service(@RequestBody TradeRequest request) {
        return goldTradeService.handle(request);
    }
}
