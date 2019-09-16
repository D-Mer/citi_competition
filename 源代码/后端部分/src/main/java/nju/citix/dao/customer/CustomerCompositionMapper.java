package nju.citix.dao.customer;

import nju.citix.po.CustomerComposition;

import java.util.List;

/**
 * @author zhm
 */
public interface CustomerCompositionMapper {
    /**
     * 插入用户购买的基金组合
     *
     * @param customerComposition 用户购买的基金组合
     * @return
     */
    Integer insertCompositionOfCustomer(CustomerComposition customerComposition);

    /**
     * 根据基金组合id查找用户购买的基金组合
     *
     * @param compositionId 基金组合id
     */
    CustomerComposition selectCompositionOfCustomerById(Integer compositionId);

    /**
     * 删除用户购买的基金组合
     *
     * @param compositionId 基金组合id
     */
    Integer deleteCompositionOfCustomerById(Integer compositionId);

    /**
     * 更新用户购买的基金组合
     *
     * @param customerComposition 用户购买的基金组合
     */
    Integer updateCompositionOfCustomer(CustomerComposition customerComposition);

    /**
     * 查找所有待卖出的基金组合
     *
     * @return 所有待卖出基金组合
     */
    List<CustomerComposition> selectPendingCompositions();
}
