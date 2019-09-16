package nju.citix.po;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Fund {
    /**
     * 基金在本数据库中的编号
     */
    private Integer fundId;

    /**
     * 基金代码
     */
    @Size(min = 6, max = 6)
    private String fundCode;

    /**
     * 基金名称
     */
    private String fundName;

    /**
     * 基金简称
     */
    private String abbreviation;

    /**
     * 基金简称拼音
     */
    private String pinyin;

    /**
     * 设立日期
     */
    private LocalDate startTime;

    /**
     * 基金类型
     */
    private String type;

    /**
     * 基金设立规模(份）
     */
    @Min(0)
    private BigDecimal scale;

    /**
     * 基金管理公司（基金管理人）
     */
    private String managerCompany;

    /**
     * 基金管理银行(基金托管人)
     */
    private String managerBank;

    /**
     * 基金历史
     */
    private String fundHistory;

    /**
     * 投资类型
     */
    private String investType;

    /**
     * 投资目标
     */
    private String target;

    /**
     * 最低申购金额
     */
    @Min(0)
    private BigDecimal minPurchaseAmount;

    /**
     * 投资范围
     */
    private String fundRange;

    /**
     * 最低持有份额
     */
    @Min(0)
    private BigDecimal minPart;

    /**
     * 基金经理
     */
    private String manager;

    /**
     * 基金经理链接（基金经理详细信息）
     */
    @URL(message = "链接格式错误")
    private String managerLink;

    /**
     * 详细信息链接（详细数据）
     */
    @URL(message = "链接格式错误")
    private String url;
}