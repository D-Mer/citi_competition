package nju.citix.dao.fund;

import nju.citix.po.FundComposition;

import java.util.List;

/**
 * @author zhm
 */
public interface FundCompositionMapper {
    /**
     * 插入一个基金组合条目
     *
     * @param fundComposition 基金组合条目
     */
    Integer insertFundCompositionItem(FundComposition fundComposition);

    /**
     * 查找基金组合
     *
     * @param compositionId 基金组合id
     * @return 基金组合，使用基金组合条目的List表示
     */
    List<FundComposition> selectFundCompositionByCompositionId(Integer compositionId);

    /**
     * 更新基金组合条目
     *
     * @param fundComposition 基金组合条目
     */
    Integer updateFundCompositionItem(FundComposition fundComposition);

    /**
     * 删除基金组合
     * @param compositionId 基金组合id
     */
    Integer deleteFundCompositionByCompositionId(Integer compositionId);

}
