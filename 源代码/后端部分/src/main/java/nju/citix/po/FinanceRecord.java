package nju.citix.po;

import lombok.Data;
import nju.citix.utils.AlipayUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jiang hui
 */
@Data
public class FinanceRecord {

    private Integer id;

    private Integer customerId;

    private String tradeNum;

    private String remark;

    private BigDecimal amount;

    private LocalDateTime tradeTime;

    private String state;

    public FinanceRecord(){}

    public FinanceRecord(int customerId, String remark, BigDecimal amount, String state){
        this.customerId = customerId;
        this.tradeNum = AlipayUtil.getOutBizNum(customerId);
        this.remark = remark;
        this.amount = amount;
        this.state = state;
        this.tradeTime = LocalDateTime.now();
    }

    public FinanceRecord(int customerId, String remark, BigDecimal amount, String out_trade_no, String state){
        this.customerId = customerId;
        this.tradeNum = out_trade_no;
        this.remark = remark;
        this.amount = amount;
        this.state = state;
        this.tradeTime = LocalDateTime.now();
    }

}
