package nju.citix.service.fund;

import nju.citix.po.Fund;
import nju.citix.po.FundBuyRate;
import nju.citix.po.FundNetValue;
import nju.citix.vo.FundForm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author GKY
 */
public interface FundService {
    /**
     * 增加基金
     *
     * @param record 基金的实体类
     * @return
     */
    Fund addFund(FundForm record);

    /**
     * 修改基金
     *
     * @param record 基金
     * @return
     */
    Fund changeFund(Fund record);

    /**
     * 查看所有基金信息（分页）
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 每页中包含的基金信息的数量，大于等于1
     * @return 指定页的所有基金信息
     */
    List<Fund> viewAllFunds(Integer pageNum, Integer pageSize);

    /**
     * 根据用户信息生成基金推荐列表（目前此方案仅针对老用户）
     *
     * @param customerId 消费者id
     * @return 基金推荐列表
     */
    List<Fund> recommendFund(Integer customerId);

    List<FundBuyRate> getFundBuyRate(Integer fundId);

    Fund findFundById(Integer fundId);

    Fund findFundByCode(String fundCode);

    /**
     * 管理员和用户查看基金净值
     *
     * @param fundId 对应基金代码
     * @return 指定基金的净值列表
     */
    List<FundNetValue> getFundNetValue(Integer fundId);

    /**
     * 查看推荐的基金组合配比
     *
     * @param customerId 用户id
     * @param funds      用户选择的基金
     * @return 返回基金配比组合，key:基金id，value:基金占比
     */
    Map<Integer, BigDecimal> getFundComposition(Integer customerId, List<Integer> funds);

    /**
     * 获取默认基金组合信息
     */
    List<Map<Integer, BigDecimal>> getDefaultFundComposition();

}
