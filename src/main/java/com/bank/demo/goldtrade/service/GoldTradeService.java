package com.bank.demo.goldtrade.service;

import com.bank.demo.goldtrade.dto.TradeRequest;
import com.bank.demo.goldtrade.dto.TradeResponse;

/**
 * 黄金交易服务接口
 */
public interface GoldTradeService {

    /** 统一入口：按交易码分发处理 */
    TradeResponse handle(TradeRequest request);
}
