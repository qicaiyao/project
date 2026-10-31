package com.bank.demo.goldtrade.service;

import com.bank.demo.goldtrade.dto.PositionItem;
import com.bank.demo.goldtrade.dto.TradeDetailItem;
import com.bank.demo.goldtrade.dto.TradeRequest;
import com.bank.demo.goldtrade.dto.TradeResponse;
import com.bank.demo.goldtrade.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 黄金交易服务实现（演示版）
 * <p>
 * 说明：
 * 1. 金价为模拟行情（围绕基础价随机波动），生产环境应替换为上金所/行情源实时接入；
 * 2. 持仓与流水保存在内存 Map，生产环境应落库（如 Oracle/MySQL + 事务）；
 * 3. 资金扣划、验签、幂等控制等生产要素见接口文档「生产化改造清单」。
 */
@Service
public class GoldTradeServiceImpl implements GoldTradeService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 交易码 */
    private static final String TXN_PRICE = "1001";
    private static final String TXN_BUY = "1002";
    private static final String TXN_SELL = "1003";
    private static final String TXN_POSITION = "1004";
    private static final String TXN_DETAIL = "1005";

    @Value("${gold.trade.fee-rate:0.0008}")
    private BigDecimal feeRate;

    @Value("${gold.trade.base-price:615.50}")
    private BigDecimal basePrice;

    @Value("${gold.trade.min-weight:1.00}")
    private BigDecimal minWeight;

    /** 行情时间戳 */
    private volatile long priceTime = System.currentTimeMillis();

    /** 当前金价（元/克）：客户买入价 = 卖出价 + 点差，模拟 */
    private volatile BigDecimal sellPrice = basePrice;

    /** 持仓表：custId -> (goldCode -> weight) */
    private final Map<String, Map<String, BigDecimal>> positions = new ConcurrentHashMap<>();

    /** 持仓成本表：custId -> (goldCode -> totalCost) */
    private final Map<String, Map<String, BigDecimal>> positionCosts = new ConcurrentHashMap<>();

    /** 交易流水：custId -> 流水列表 */
    private final Map<String, List<TradeDetailItem>> txns = new ConcurrentHashMap<>();

    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public TradeResponse handle(TradeRequest req) {
        checkCommon(req);
        return switch (req.getTxnCode()) {
            case TXN_PRICE -> queryPrice(req);
            case TXN_BUY -> buy(req);
            case TXN_SELL -> sell(req);
            case TXN_POSITION -> queryPosition(req);
            case TXN_DETAIL -> queryDetail(req);
            default -> throw new BizException("E1005", "不支持的交易码：" + req.getTxnCode());
        };
    }

    // ------------------------------------------------------------------
    // 1001 金价查询
    // ------------------------------------------------------------------
    private TradeResponse queryPrice(TradeRequest req) {
        refreshPrice();
        TradeResponse resp = ok();
        resp.setGoldCode("AU9999");
        // 客户买入价 = 卖出价 + 1.2 元点差（模拟）
        resp.setBuyPrice(sellPrice.add(new BigDecimal("1.20")));
        resp.setSellPrice(sellPrice);
        resp.setPriceTime(LocalDateTime.now().format(TS));
        return resp;
    }

    // ------------------------------------------------------------------
    // 1002 买入
    // ------------------------------------------------------------------
    private TradeResponse buy(TradeRequest req) {
        checkTradeParams(req);
        refreshPrice();
        BigDecimal dealPrice = sellPrice.add(new BigDecimal("1.20")); // 客户买入价
        BigDecimal amount = dealPrice.multiply(req.getWeight()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = amount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);

        // 更新持仓
        Map<String, BigDecimal> pos = positions.computeIfAbsent(req.getCustId(), k -> new ConcurrentHashMap<>());
        Map<String, BigDecimal> cost = positionCosts.computeIfAbsent(req.getCustId(), k -> new ConcurrentHashMap<>());
        pos.merge("AU9999", req.getWeight(), BigDecimal::add);
        cost.merge("AU9999", amount.add(fee), BigDecimal::add);

        TradeDetailItem detail = saveTxn(req, "BUY", dealPrice, amount, fee);
        return buildTradeResp(resp -> {
            resp.setOrderId(detail.getOrderId());
            resp.setDealPrice(dealPrice);
            resp.setDealAmount(amount);
            resp.setFee(fee);
        });
    }

    // ------------------------------------------------------------------
    // 1003 卖出
    // ------------------------------------------------------------------
    private TradeResponse sell(TradeRequest req) {
        checkTradeParams(req);
        refreshPrice();
        Map<String, BigDecimal> pos = positions.get(req.getCustId());
        Map<String, BigDecimal> cost = positionCosts.get(req.getCustId());
        BigDecimal held = pos == null ? BigDecimal.ZERO : pos.getOrDefault("AU9999", BigDecimal.ZERO);
        if (held.compareTo(req.getWeight()) < 0) {
            throw new BizException("E1004", "持仓不足：当前持有 " + held + " 克，卖出 " + req.getWeight() + " 克");
        }

        BigDecimal dealPrice = sellPrice;
        BigDecimal amount = dealPrice.multiply(req.getWeight()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = amount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);

        pos.merge("AU9999", req.getWeight().negate(), BigDecimal::add);
        // 成本按平均成本价等比扣减
        BigDecimal totalCost = cost.getOrDefault("AU9999", BigDecimal.ZERO);
        BigDecimal avg = held.compareTo(BigDecimal.ZERO) > 0
                ? totalCost.divide(held, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        cost.put("AU9999", totalCost.subtract(avg.multiply(req.getWeight())).setScale(2, RoundingMode.HALF_UP));

        TradeDetailItem detail = saveTxn(req, "SELL", dealPrice, amount, fee);
        return buildTradeResp(resp -> {
            resp.setOrderId(detail.getOrderId());
            resp.setDealPrice(dealPrice);
            resp.setDealAmount(amount);
            resp.setFee(fee);
        });
    }

    // ------------------------------------------------------------------
    // 1004 持仓查询
    // ------------------------------------------------------------------
    private TradeResponse queryPosition(TradeRequest req) {
        refreshPrice();
        Map<String, BigDecimal> pos = positions.get(req.getCustId());
        Map<String, BigDecimal> cost = positionCosts.get(req.getCustId());
        List<PositionItem> items = new ArrayList<>();
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal marketValue = BigDecimal.ZERO;

        if (pos != null) {
            for (Map.Entry<String, BigDecimal> e : pos.entrySet()) {
                if (e.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                PositionItem item = new PositionItem();
                item.setGoldCode(e.getKey());
                item.setWeight(e.getValue());
                BigDecimal totalCost = cost == null ? BigDecimal.ZERO : cost.getOrDefault(e.getKey(), BigDecimal.ZERO);
                item.setTotalCost(totalCost);
                item.setAvgCostPrice(e.getValue().compareTo(BigDecimal.ZERO) > 0
                        ? totalCost.divide(e.getValue(), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                item.setLatestPrice(sellPrice);
                BigDecimal mv = sellPrice.multiply(e.getValue()).setScale(2, RoundingMode.HALF_UP);
                item.setFloatingProfit(mv.subtract(totalCost));
                items.add(item);
                totalWeight = totalWeight.add(e.getValue());
                marketValue = marketValue.add(mv);
            }
        }
        final BigDecimal fTotalWeight = totalWeight;
        final BigDecimal fMarketValue = marketValue;
        return buildTradeResp(resp -> {
            resp.setPositions(items);
            resp.setTotalWeight(fTotalWeight);
            resp.setMarketValue(fMarketValue);
        });
    }

    // ------------------------------------------------------------------
    // 1005 交易明细查询
    // ------------------------------------------------------------------
    private TradeResponse queryDetail(TradeRequest req) {
        List<TradeDetailItem> all = txns.getOrDefault(req.getCustId(), List.of());
        List<TradeDetailItem> filtered = all.stream()
                .filter(t -> matchDate(t.getTxnTime(), req.getBeginDate(), req.getEndDate()))
                .sorted(Comparator.comparing(TradeDetailItem::getTxnTime).reversed())
                .toList();
        return buildTradeResp(resp -> resp.setDetails(filtered));
    }

    // ------------------------------------------------------------------
    // 内部工具
    // ------------------------------------------------------------------
    private void checkCommon(TradeRequest req) {
        if (req == null || isBlank(req.getTxnCode())) {
            throw new BizException("E1001", "交易码 txnCode 不能为空");
        }
        if (isBlank(req.getReqSeq())) {
            throw new BizException("E1001", "请求流水号 reqSeq 不能为空");
        }
        if (!"MOBILE_BANK".equals(req.getChannel())) {
            throw new BizException("E1006", "渠道标识非法，仅支持 MOBILE_BANK");
        }
    }

    private void checkTradeParams(TradeRequest req) {
        if (isBlank(req.getCustId())) {
            throw new BizException("E1001", "客户号 custId 不能为空");
        }
        if (req.getWeight() == null || req.getWeight().compareTo(minWeight) < 0) {
            throw new BizException("E1002", "交易克重不能低于最小限制 " + minWeight + " 克");
        }
    }

    private boolean matchDate(String txnTime, String beginDate, String endDate) {
        if (isBlank(beginDate) && isBlank(endDate)) {
            return true;
        }
        String d = txnTime == null ? "" : txnTime.substring(0, 8);
        if (!isBlank(beginDate) && d.compareTo(beginDate) < 0) return false;
        if (!isBlank(endDate) && d.compareTo(endDate) > 0) return false;
        return true;
    }

    private TradeDetailItem saveTxn(TradeRequest req, String direction, BigDecimal dealPrice, BigDecimal amount, BigDecimal fee) {
        TradeDetailItem detail = new TradeDetailItem();
        detail.setOrderId(genOrderId(direction));
        detail.setDirection(direction);
        detail.setGoldCode("AU9999");
        detail.setWeight(req.getWeight());
        detail.setDealPrice(dealPrice);
        detail.setDealAmount(amount);
        detail.setFee(fee);
        detail.setStatus("SUCCESS");
        detail.setTxnTime(LocalDateTime.now().format(TS));
        txns.computeIfAbsent(req.getCustId(), k -> new ArrayList<>()).add(detail);
        return detail;
    }

    /** 模拟行情：围绕基础价 ±3 元随机游走，3 秒内复用同一价格 */
    private synchronized void refreshPrice() {
        long now = System.currentTimeMillis();
        if (now - priceTime > 3000) {
            double walk = ThreadLocalRandom.current().nextDouble(-3.0, 3.0);
            sellPrice = basePrice.add(BigDecimal.valueOf(walk)).setScale(2, RoundingMode.HALF_UP);
            priceTime = now;
        }
    }

    private String genOrderId(String direction) {
        return direction + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%08d", seq.incrementAndGet());
    }

    private TradeResponse ok() {
        TradeResponse resp = new TradeResponse();
        resp.setReturnCode("000000");
        resp.setReturnMsg("交易成功");
        return resp;
    }

    @FunctionalInterface
    private interface RespBuilder { void build(TradeResponse resp); }

    private TradeResponse buildTradeResp(RespBuilder builder) {
        TradeResponse resp = ok();
        builder.build(resp);
        return resp;
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
