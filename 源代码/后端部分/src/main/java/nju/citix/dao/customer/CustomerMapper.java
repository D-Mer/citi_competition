package nju.citix.dao.customer;

import nju.citix.po.Customer;
import nju.citix.po.Questionnaire;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author DW
 */
public interface CustomerMapper {
    /**
     * 插入新用户
     *
     * @param record 只需要用户名,密码和邮箱即可，其他为null
     * @return 插入数量
     */
    int insert(Customer record);

    /**
     * 根据id查找用户
     *
     * @param customerId 用户id
     * @return 查找到的用户(或者null)
     */
    @Nullable
    Customer selectByPrimaryKey(Integer customerId);

    /**
     * 用邮箱查找用户
     *
     * @param email 用户的邮箱
     */
    @Nullable
    Customer selectByEmail(@Param("email") String email);

    /**
     * 更新用户的信息
     *
     * @param record id不可为空;其他需要修改的数据不为空，不需要修改的数据为null
     */
    int updateByPrimaryKeySelective(Customer record);

    /**
     * 根据用户邮箱更新密码
     *
     * @param updatedPassword 新的密码
     * @param email           邮箱
     */
    int updatePasswordByEmail(@Param("updatedPassword") String updatedPassword, @Param("email") String email);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     */
    @Nullable
    Customer selectByUsername(String username);

    /**
     * 添加用户填写的问卷信息
     *
     * @param questionnaire 问卷信息
     */
    int insertQuestionnaire(Questionnaire questionnaire);

    /**
     * 更新用户填写的问卷信息
     *
     * @param questionnaire 问卷信息
     */
    int updateQuestionnaire(Questionnaire questionnaire);

    /**
     * 获取相应用户填写的问卷
     *
     * @param customerId 消费者的id
     * @return 用户填写的新的问卷信息
     */
    Questionnaire selectQuestionnaireByCustomerId(Integer customerId);

    /**
     * 根据分页信息查消费者
     *
     * @param offset  偏移量
     * @param rowsNum 需要的数据的行数
     * @return 查找到的消费者
     */
    List<Customer> selectAllByPage(@Param("offset") Integer offset, @Param("rowsNum") Integer rowsNum);
}