package nju.citix.service.finance;

import nju.citix.po.FinanceRecord;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author jiang hui
 */
public interface FinanceServiceForBL {

    /**
     * 记录用户余额变动信息
     *
     * @param customerId 用户id
     * @param amount     变动金额，必须为正数
     * @param remark     变动原因，有
     *                   充值：RECHARGE_REMARK
     *                   提现：WITHDRAW_REMARK
     *                   买入：BUY_REMARK
     *                   赎回：REDEMPTION_REMARK
     * @return 变动记录
     * @throws Exception getMessage()可以获得错误信息
     */
    @Transactional
    FinanceRecord addBalanceChangeRecord(int customerId, BigDecimal amount, String remark) throws Exception;

}
