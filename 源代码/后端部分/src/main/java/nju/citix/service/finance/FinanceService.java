package nju.citix.service.finance;

import nju.citix.po.CustomerComposition;
import nju.citix.po.FinanceRecord;
import nju.citix.po.PurchaseRecord;
import nju.citix.vo.CompositionDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author jiang hui
 * @date 2019/8/28
 */

public interface FinanceService {

    /**
     * 充值
     *
     * @param customerId   消费者id
     * @param amount       充值金额
     * @param out_trade_no 订单号
     * @param network      是否联网
     * @return 充值记录信息，如果没联网则记录state为"已完成"
     */
    FinanceRecord recharge(Integer customerId, BigDecimal amount, String out_trade_no, boolean network);

    /**
     * 提现
     *
     * @param customerId 消费者id
     * @param amount     提现金额
     * @param network    是否联网，该参数暂时没用
     * @return 提现操作结果，成功则返回true
     */
    boolean withdraw(Integer customerId, BigDecimal amount, boolean network);

    /**
     * 完成消费(暂时只有充值)记录并更新用户余额
     *
     * @param tradeNum 消费记录号(订单号)
     * @return 成功则返回true
     */
    boolean completeTrade(String tradeNum);

    /**
     * 校验用户信息，暂时只校验用户是否存在
     *
     * @param customerId 用户id
     * @return 成功则返回true
     */
    boolean validateCustomer(Integer customerId);

    /**
     * 查看对应用户的所有余额变动记录
     *
     * @param customerId 用户id
     * @return 所有余额变动记录列表
     */
    List<FinanceRecord> viewAllRecords(Integer customerId);

    /**
     * 卖出基金组合
     *
     * @param compositionId 基金组合id
     * @return 基金组合
     */
    CustomerComposition sellFundComposition(Integer compositionId);

    /**
     * 确认基金组合
     *
     * @param customerId      消费者id
     * @param fundComposition 基金组合，key：基金编号，value：基金所占百分比
     * @return 基金组合id
     */
    Integer confirmFundComposition(Integer customerId, Map<Integer, BigDecimal> fundComposition);

    /**
     * 购买基金组合
     *
     * @param compositionId  基金组合id
     * @param purchaseAmount 购买金额
     * @return true表示购买成功
     */
    boolean buyFundComposition(Integer customerId, Integer compositionId, BigDecimal purchaseAmount);

    /**
     * 根据组合ID查找用户的组合详情
     *
     * @param compositionId 组合ID
     * @return 相应基金组合详情
     */
    CompositionDetail getPurchaseRecordByCompositionId(Integer compositionId);

    /**
     * 根据用户ID查找购买记录,按是否卖出排序，已卖出的在前（默认按卖出日期升序排序），未卖出的在后（默认按买入日期升序排序）
     *
     * @param customerId 顾客id
     * @return 相应购买记录列表
     */
    List<PurchaseRecord> getPurchaseRecordsByCustomerIdInSoldTime(Integer customerId);

    /**
     * 根据用户ID查找购买记录,按买入日期降序排序
     *
     * @param customerId 消费者id
     * @return 相应购买记录列表
     */
    List<PurchaseRecord> getPurchaseRecordsByCustomerIdInPurchaseTime(Integer customerId);

    /**
     * 根据用户ID查找购买记录,按买入金额降序排序
     *
     * @param customerId 消费者id
     * @return 相应购买记录列表
     */
    List<PurchaseRecord> getPurchaseRecordsByCustomerIdInPurchaseAmount(Integer customerId);


}
