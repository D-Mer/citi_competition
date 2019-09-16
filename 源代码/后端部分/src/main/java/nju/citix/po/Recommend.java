package nju.citix.po;

import lombok.Data;

@Data
public class Recommend {

    private Integer customerId;

    private String fundCode;

    public Recommend(){}

    public Recommend(Integer customerId, String fundCode){
        this.customerId = customerId;
        this.fundCode = fundCode;
    }
}
