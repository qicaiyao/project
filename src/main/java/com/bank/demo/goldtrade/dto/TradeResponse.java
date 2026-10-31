package com.bank.demo.goldtrade.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统一响应报文。不同交易码按需填充对应字段，未用到的字段不输出。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "response")
public class TradeResponse {

    /** 返回码：000000 成功，其余见错误码表 */
    @JacksonXmlProperty(localName = "returnCode")
    private String returnCode;

    /** 返回描述 */
    @JacksonXmlProperty(localName = "returnMsg")
    private String returnMsg;

    // ---------- 1001 金价查询 ----------
    @JacksonXmlProperty(localName = "goldCode")
    private String goldCode;

    /** 客户买入价（元/克） */
    @JacksonXmlProperty(localName = "buyPrice")
    private BigDecimal buyPrice;

    /** 客户卖出价（元/克） */
    @JacksonXmlProperty(localName = "sellPrice")
    private BigDecimal sellPrice;

    /** 行情时间 yyyyMMddHHmmss */
    @JacksonXmlProperty(localName = "priceTime")
    private String priceTime;

    // ---------- 1002/1003 买入/卖出 ----------
    @JacksonXmlProperty(localName = "orderId")
    private String orderId;

    /** 成交价（元/克） */
    @JacksonXmlProperty(localName = "dealPrice")
    private BigDecimal dealPrice;

    /** 成交金额（元） */
    @JacksonXmlProperty(localName = "dealAmount")
    private BigDecimal dealAmount;

    /** 手续费（元） */
    @JacksonXmlProperty(localName = "fee")
    private BigDecimal fee;

    /** 交易方向：BUY / SELL */
    @JacksonXmlProperty(localName = "direction")
    private String direction;

    // ---------- 1004 持仓查询 ----------
    @JacksonXmlElementWrapper(localName = "positions")
    @JacksonXmlProperty(localName = "position")
    private List<PositionItem> positions;

    /** 持仓总克重 */
    @JacksonXmlProperty(localName = "totalWeight")
    private BigDecimal totalWeight;

    /** 持仓总市值（按最新卖出价估值） */
    @JacksonXmlProperty(localName = "marketValue")
    private BigDecimal marketValue;

    // ---------- 1005 交易明细 ----------
    @JacksonXmlElementWrapper(localName = "details")
    @JacksonXmlProperty(localName = "detail")
    private List<TradeDetailItem> details;

    public String getReturnCode() { return returnCode; }
    public void setReturnCode(String returnCode) { this.returnCode = returnCode; }
    public String getReturnMsg() { return returnMsg; }
    public void setReturnMsg(String returnMsg) { this.returnMsg = returnMsg; }
    public String getGoldCode() { return goldCode; }
    public void setGoldCode(String goldCode) { this.goldCode = goldCode; }
    public BigDecimal getBuyPrice() { return buyPrice; }
    public void setBuyPrice(BigDecimal buyPrice) { this.buyPrice = buyPrice; }
    public BigDecimal getSellPrice() { return sellPrice; }
    public void setSellPrice(BigDecimal sellPrice) { this.sellPrice = sellPrice; }
    public String getPriceTime() { return priceTime; }
    public void setPriceTime(String priceTime) { this.priceTime = priceTime; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public BigDecimal getDealPrice() { return dealPrice; }
    public void setDealPrice(BigDecimal dealPrice) { this.dealPrice = dealPrice; }
    public BigDecimal getDealAmount() { return dealAmount; }
    public void setDealAmount(BigDecimal dealAmount) { this.dealAmount = dealAmount; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public List<PositionItem> getPositions() { return positions; }
    public void setPositions(List<PositionItem> positions) { this.positions = positions; }
    public BigDecimal getTotalWeight() { return totalWeight; }
    public void setTotalWeight(BigDecimal totalWeight) { this.totalWeight = totalWeight; }
    public BigDecimal getMarketValue() { return marketValue; }
    public void setMarketValue(BigDecimal marketValue) { this.marketValue = marketValue; }
    public List<TradeDetailItem> getDetails() { return details; }
    public void setDetails(List<TradeDetailItem> details) { this.details = details; }
}
