package nju.citix.po;

import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.sql.Date;

@Data
public class FundNetValue {
    /**
     * 对应基金代码
     */
    private Integer fundId;

    /**
     * 交易日
     */
    private Date tradingTime;

    /**
     * 最新净值
     */
    @Min(0)
    private BigDecimal latestValue;

    /**
     * 日回报:百分比（%）
     */
    @Min(0)
    private BigDecimal dailyReturn;

    /**
     * 周回报:百分比（%）
     */
    @Min(0)
    private BigDecimal weeklyReturn;

    /**
     * 月回报:百分比（%）
     */
    @Min(0)
    private BigDecimal monthlyReturn;

    /**
     * 三月回报:百分比（%）
     */
    @Min(0)
    private BigDecimal threeMonthsReturn;

}
