package nju.citix.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeRecord {

    private Integer customerId;

    private LocalDateTime tradeTime;

    private BigDecimal amount;

    private String fundType;

    private String fundCode;

    public TradeRecord(){
    }

    public TradeRecord(Integer customerId, LocalDateTime tradeTime, BigDecimal amount, String fundType, String fundCode){
        this.customerId = customerId;
        this.tradeTime = tradeTime;
        this.amount = amount;
        this.fundType = fundType;
        this.fundCode = fundCode;
    }
}
