package com.bank.demo.goldtrade.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

/** 持仓明细项（交易码 1004） */
public class PositionItem {

    @JacksonXmlProperty(localName = "goldCode")
    private String goldCode;

    /** 持有克重 */
    @JacksonXmlProperty(localName = "weight")
    private BigDecimal weight;

    /** 持仓成本（元） */
    @JacksonXmlProperty(localName = "totalCost")
    private BigDecimal totalCost;

    /** 平均成本价（元/克） */
    @JacksonXmlProperty(localName = "avgCostPrice")
    private BigDecimal avgCostPrice;

    /** 最新卖出价（元/克） */
    @JacksonXmlProperty(localName = "latestPrice")
    private BigDecimal latestPrice;

    /** 浮动盈亏（元）= 市值 - 成本 */
    @JacksonXmlProperty(localName = "floatingProfit")
    private BigDecimal floatingProfit;

    public String getGoldCode() { return goldCode; }
    public void setGoldCode(String goldCode) { this.goldCode = goldCode; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public BigDecimal getAvgCostPrice() { return avgCostPrice; }
    public void setAvgCostPrice(BigDecimal avgCostPrice) { this.avgCostPrice = avgCostPrice; }
    public BigDecimal getLatestPrice() { return latestPrice; }
    public void setLatestPrice(BigDecimal latestPrice) { this.latestPrice = latestPrice; }
    public BigDecimal getFloatingProfit() { return floatingProfit; }
    public void setFloatingProfit(BigDecimal floatingProfit) { this.floatingProfit = floatingProfit; }
}
