package nju.citix.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhm
 * @date 2019/9/4
 */
@Data
@NoArgsConstructor
public class CustomerComposition {
    /**
     * 基金组合id
     */
    private Integer compositionId;

    /**
     * 消费者id
     */
    private Integer customerId;

    /**
     * 购买金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 卖出金额
     */
    private BigDecimal soldAmount;

    /**
     * 购买时间
     */
    private LocalDateTime purchaseTime;

    /**
     * 卖出时间
     */
    private LocalDateTime soldTime;

    /**
     * 申请卖出时间
     */
    private LocalDateTime requestTime;

    /**
     * 使用customerId初始化
     * @param customerId 消费者id
     */
    public CustomerComposition(Integer customerId) {
        this.customerId = customerId;
    }

    /**
     * 默认初始化
     */
    public CustomerComposition(Integer compositionId, Integer customerId, BigDecimal purchaseAmount, BigDecimal soldAmount,
                               LocalDateTime purchaseTime, LocalDateTime soldTime, LocalDateTime requestTime) {
        this.compositionId = compositionId;
        this.customerId = customerId;
        this.purchaseAmount = purchaseAmount;
        this.soldAmount = soldAmount;
        this.purchaseTime = purchaseTime;
        this.soldTime = soldTime;
        this.requestTime = requestTime;
    }
}
