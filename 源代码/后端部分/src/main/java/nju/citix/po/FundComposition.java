package nju.citix.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhm
 * @date 2019/8/31
 */
@Data
@NoArgsConstructor
public class FundComposition {
    /**
     * 基金组合id
     */
    Integer compositionId;

    /**
     * 基金id
     */
    Integer fundId;

    /**
     * 基金的所占百分比
     */
    BigDecimal fundPercentage;

    /**
     * 基金的份额
     */
    BigDecimal fundShare;

    /**
     * 购买基金扣除的费率
     */
    BigDecimal purchaseLoss;

    /**
     * 赎回基金扣除的费率
     */
    BigDecimal soldLoss;

    public FundComposition(Integer compositionId, Integer fundId, BigDecimal fundPercentage) {
        this.compositionId = compositionId;
        this.fundId = fundId;
        this.fundPercentage = fundPercentage;
    }

    public FundComposition(Integer compositionId, Integer fundId, BigDecimal fundPercentage,
                           BigDecimal fundShare, BigDecimal purchaseLoss, BigDecimal soldLoss) {
        this.compositionId = compositionId;
        this.fundId = fundId;
        this.fundPercentage = fundPercentage;
        this.fundShare = fundShare;
        this.purchaseLoss = purchaseLoss;
        this.soldLoss = soldLoss;
    }
}

