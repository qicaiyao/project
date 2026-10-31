package com.bank.demo.goldtrade.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

/** 交易明细项（交易码 1005） */
public class TradeDetailItem {

    @JacksonXmlProperty(localName = "orderId")
    private String orderId;

    @JacksonXmlProperty(localName = "direction")
    private String direction;

    @JacksonXmlProperty(localName = "goldCode")
    private String goldCode;

    @JacksonXmlProperty(localName = "weight")
    private BigDecimal weight;

    @JacksonXmlProperty(localName = "dealPrice")
    private BigDecimal dealPrice;

    @JacksonXmlProperty(localName = "dealAmount")
    private BigDecimal dealAmount;

    @JacksonXmlProperty(localName = "fee")
    private BigDecimal fee;

    @JacksonXmlProperty(localName = "status")
    private String status;

    @JacksonXmlProperty(localName = "txnTime")
    private String txnTime;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getGoldCode() { return goldCode; }
    public void setGoldCode(String goldCode) { this.goldCode = goldCode; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public BigDecimal getDealPrice() { return dealPrice; }
    public void setDealPrice(BigDecimal dealPrice) { this.dealPrice = dealPrice; }
    public BigDecimal getDealAmount() { return dealAmount; }
    public void setDealAmount(BigDecimal dealAmount) { this.dealAmount = dealAmount; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTxnTime() { return txnTime; }
    public void setTxnTime(String txnTime) { this.txnTime = txnTime; }
}
