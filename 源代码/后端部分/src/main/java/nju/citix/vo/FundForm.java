package nju.citix.vo;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * @author GKY
 * @date 2019/8/20
 */
@Data
public class FundForm {
    /**
     * 基金代码
     */
    @Size(min = 6, max = 6)
    private String fundCode;
    /**
     * 最低申购金额
     */
    private BigDecimal minPurchaseAmount;
    /**
     * 最低持有份额
     */
    private BigDecimal minPart;
    /**
     * 详细信息链接（详细数据）
     */
    @URL(message = "链接格式错误")
    private String url;
}
