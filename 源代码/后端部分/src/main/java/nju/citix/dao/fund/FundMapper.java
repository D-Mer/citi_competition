package nju.citix.dao.fund;

import nju.citix.po.*;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FundMapper {
    int insert(Fund record);

    int updateByPrimaryKey(Fund record);

    @Nullable
    Fund selectFundByPrimaryKey(Integer fundId);

    @Nullable
    Fund selectFundByCode(String fundCode);

    /**
     * 根据分页信息查找基金
     *
     * @param offset  偏移量（索引值）（默认从0开始）
     * @param rowsNum 需要的数据的行数
     * @return 查找到的基金list
     */
    @Nullable
    List<Fund> selectAllFundByPage(Integer offset, Integer rowsNum);

    /**
     * 查找基金排序列表（按code排序，前10个）
     *
     * @return 查找到的基金list
     */
    @Nullable
    List<Fund> selectFundListOrderedByCodeTopTen();

    /**
     * 查找基金净值
     *
     * @param fundId 对应基金代码
     * @return 指定基金的基金净值列表
     */
    @Nullable
    List<FundNetValue> selectFundNetValue(Integer fundId);

    @Nullable
    List<FundBuyRate> selectFundBuyRateById(Integer fundId);

    @Nullable
    FundNetValue selectNewestFundNetValue(Integer fundId);

    @Nullable
    List<FundOut> selectFundOutById(Integer fundId);

    int insertRecommendList(@Param("recommendList") List<Recommend> recommendList);

    int deleteRecommendList();

    List<Fund> selectRecommendListByUserId(@Param("customerId") Integer customerId);

    List<Fund> selectAllByFundIdIn(@Param("fundIds") List<Integer> fundIds);

}