package nju.citix.po;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FundBuyRate {
    /**
     * 基金在本数据库中的编号
     */
    private Integer fundId;
    /**
     * 费率描述类型：‘0’表示按百分比描述，‘1’表示按每单xx元描述
     */
    private Integer descriptionType;
    /**
     * 起步金额（万元)
     */
    @Min(0)
    private Integer startAmount;
    /**
     * 最高费率
     */
    @Min(0)
    private BigDecimal rate;
}