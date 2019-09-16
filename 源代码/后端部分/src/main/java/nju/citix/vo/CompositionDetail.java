package nju.citix.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nju.citix.po.Fund;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author DW
 * @date 2019/9/13
 */
@Data
public class CompositionDetail {

    private Integer compositionId;
    private ArrayList<FundWithPercentage> fundsWithPercentage = new ArrayList<>();

    @Data
    public static class FundWithPercentage {
        private BigDecimal percentage;
        private Fund fund;
    }
}
