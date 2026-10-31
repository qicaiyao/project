package com.bank.demo.goldtrade.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.math.BigDecimal;

/**
 * 统一请求报文（扁平结构，与银行网关风格一致）。
 * <p>
 * 报文示例：
 * <pre>
 * &lt;request&gt;
 *   &lt;txnCode&gt;1001&lt;/txnCode&gt;
 *   &lt;reqSeq&gt;20261031000001&lt;/reqSeq&gt;
 *   &lt;txnTime&gt;20261031220000&lt;/txnTime&gt;
 *   &lt;channel&gt;MOBILE_BANK&lt;/channel&gt;
 *   &lt;custId&gt;C0001&lt;/custId&gt;
 *   &lt;accountId&gt;622200001&lt;/accountId&gt;
 *   &lt;goldCode&gt;AU9999&lt;/goldCode&gt;
 *   &lt;weight&gt;10.00&lt;/weight&gt;
 * &lt;/request&gt;
 * </pre>
 */
@JacksonXmlRootElement(localName = "request")
public class TradeRequest {

    /** 交易码：1001 金价查询 / 1002 买入 / 1003 卖出 / 1004 持仓查询 / 1005 交易明细查询 */
    @JacksonXmlProperty(localName = "txnCode")
    private String txnCode;

    /** 请求流水号，手机银行生成，全局唯一 */
    @JacksonXmlProperty(localName = "reqSeq")
    private String reqSeq;

    /** 交易时间 yyyyMMddHHmmss */
    @JacksonXmlProperty(localName = "txnTime")
    private String txnTime;

    /** 渠道标识，固定 MOBILE_BANK */
    @JacksonXmlProperty(localName = "channel")
    private String channel;

    /** 客户号 */
    @JacksonXmlProperty(localName = "custId")
    private String custId;

    /** 银行账户号（资金扣划/入账账户） */
    @JacksonXmlProperty(localName = "accountId")
    private String accountId;

    /** 黄金品种代码，如 AU9999 */
    @JacksonXmlProperty(localName = "goldCode")
    private String goldCode;

    /** 交易克重（元/克 * 克），1002/1003 必输 */
    @JacksonXmlProperty(localName = "weight")
    private BigDecimal weight;

    /** 查询开始日期 yyyyMMdd，1005 可选 */
    @JacksonXmlProperty(localName = "beginDate")
    private String beginDate;

    /** 查询结束日期 yyyyMMdd，1005 可选 */
    @JacksonXmlProperty(localName = "endDate")
    private String endDate;

    /** 签名字段（示例工程未启用验签，生产环境必须校验） */
    @JacksonXmlProperty(localName = "sign")
    private String sign;

    public String getTxnCode() { return txnCode; }
    public void setTxnCode(String txnCode) { this.txnCode = txnCode; }
    public String getReqSeq() { return reqSeq; }
    public void setReqSeq(String reqSeq) { this.reqSeq = reqSeq; }
    public String getTxnTime() { return txnTime; }
    public void setTxnTime(String txnTime) { this.txnTime = txnTime; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getCustId() { return custId; }
    public void setCustId(String custId) { this.custId = custId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getGoldCode() { return goldCode; }
    public void setGoldCode(String goldCode) { this.goldCode = goldCode; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getBeginDate() { return beginDate; }
    public void setBeginDate(String beginDate) { this.beginDate = beginDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getSign() { return sign; }
    public void setSign(String sign) { this.sign = sign; }
}
