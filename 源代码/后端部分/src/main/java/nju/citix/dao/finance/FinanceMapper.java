package nju.citix.dao.finance;


import nju.citix.po.FinanceRecord;
import nju.citix.po.PurchaseRecord;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author jiang hui
 * @date 2019/8/29
 */

public interface FinanceMapper {

    Integer insertRecord(FinanceRecord record);

    Integer updateRecordSelective(FinanceRecord record);

    FinanceRecord selectByTradeNum(String tradeNum);

    FinanceRecord selectById(Integer id);

    void updateCustomerBalance(Integer customerId, BigDecimal amount);

    List<FinanceRecord> selectAllRecordByCustomerId(Integer customerId);

    /**
     * 根据用户ID查找购买记录,按买入金额降序排序
     * @param customerId 消费者id
     * @return 相应购买记录列表
     */
    List<PurchaseRecord> selectPurchaseRecordsByCustomerIdInPurchaseAmount(Integer customerId);

    /**
     * 根据用户ID查找购买记录,按买入日期降序排序
     * @param customerId 消费者id
     * @return 相应购买记录列表
     */
    List<PurchaseRecord> selectPurchaseRecordsByCustomerIdInPurchaseTime(Integer customerId);

    /**
     * 根据用户ID查找购买记录,按是否卖出排序，已卖出的在前（默认按卖出日期升序排序），未卖出的在后（默认按买入日期升序排序）
     * @param customerId 消费者id
     * @return 相应购买记录列表
     */
    List<PurchaseRecord> selectPurchaseRecordsByCustomerIdInSoldTime(Integer customerId);

    /**
     * 根据组合ID查找用户的组合详情
     * @param compositionId 组合ID
     * @return 相应基金组合详情
     */
    PurchaseRecord selectPurchaseRecordByCompositionId(Integer compositionId);
}
