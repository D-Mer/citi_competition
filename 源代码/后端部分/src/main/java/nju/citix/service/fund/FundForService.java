package nju.citix.service.fund;

import nju.citix.po.*;

import java.util.List;


public interface FundForService {

    /**
     * 查找基金最新净值
     *
     * @param fundId 基金id
     * @return 基金最新净值信息
     */
    FundNetValue getNewestFundNetValue(Integer fundId);

    /**
     * 查找基金卖出费率
     *
     * @param fundId 基金id
     * @return 基金卖出费率信息
     */
    List<FundOut> getFundOut(Integer fundId);

    /**
     * 根据基金id查找基金
     *
     * @param fundId 基金id
     * @return 基金
     */
    Fund findFundById(Integer fundId);

    /**
     * 根据基金id查找基金的买入费率
     *
     * @param fundId 基金id
     * @return 基金买入费率的list
     */
    List<FundBuyRate> getFundBuyRate(Integer fundId);

    /**
     * 管理员和用户查看基金净值
     *
     * @param fundId 对应基金代码
     * @return 指定基金的净值列表
     */
    List<FundNetValue> getFundNetValue(Integer fundId);

    void deleteRecommendList();

    void insertRecommendList(List<Recommend> recommendList);

    List<Fund> getRecommendListByUserId(Integer customerId);

    /**
     * 根据购买记录中多条fundID查找对应fund
     */
    List<Fund> getFundFromFundIds(List<Integer> fundIds);


}
