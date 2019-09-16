package nju.citix.po;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class PurchaseRecord {
    /**
     * 基金组合id
     */
    private Integer compositionId;

    /**
     * 消费者id
     */
    private Integer customerId;

    /**
     * 买入金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 卖出金额
     */
    private BigDecimal soldAmount;

    /**
     * 买入时间
     */
    private Date purchaseTime;

    /**
     * 卖出时间
     */
    private Date soldTime;

    /**
     * 申请交易时间
     */
    private Date requestTime;
}
