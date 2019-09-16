package nju.citix.po;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class FundOut {
    /**
     * 基金编号
     */
    private Integer fundId;

    /**
     * 计算费率的起始天数
     */
    private Integer startDays;

    /**
     * 对应抽取的比例
     */
    private BigDecimal rate;
}
