package nju.citix.service.customer;

import nju.citix.po.Customer;
import nju.citix.vo.QuestionnaireForm;
import nju.citix.vo.QuestionnaireVO;
import nju.citix.vo.UserForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.mail.MessagingException;

/**
 * @author DW
 */
public interface CustomerService {
    /**
     * 用户注册
     *
     * @param record 用户的实体类
     */
    @NotNull
    Customer register(UserForm record);

    /**
     * 用户点击忘记密码，提供邮箱发送重置链接
     *
     * @param email 用户的邮箱
     */
    String forget(String email) throws MessagingException;

    /**
     * 根据邮箱链接找回密码
     *
     * @param findId   找回编号
     * @param email    邮箱
     * @param code     验证码
     * @param userForm 要修改的值
     */
    boolean findPassword(int findId, String email, String code, UserForm userForm);

    /**
     * 用户验证邮箱，发送验证链接到邮箱
     *
     * @param customId 用户编号
     * @return false:已经验证过了
     * @throws MessagingException 通讯异常
     */
    String verify(int customId) throws MessagingException;

    /**
     * 消费者的登录验证
     *
     * @param username 消费者用户名
     * @param password 消费者密码
     * @param ip       用户请求的ip地址
     * @return 如果验证成功则返回对应的消费者对象，失败则返回null
     */
    @Nullable
    Customer login(String username, String password, String ip);

    /**
     * 根据id查找消费者
     *
     * @param id 消费者id
     * @return 相应消费者的对象
     */
    @Nullable
    Customer findCustomerById(int id);

    /**
     * 增加相应用户填写的问卷
     *
     * @param form 消费者填写的问卷的表单
     * @return 加上id和得分后的问卷对象，如果用户id不存在则返回null
     */
    QuestionnaireVO addQuestionnaire(QuestionnaireForm form);

    /**
     * 更新相应用户填写的问卷
     *
     * @param form 消费者填写的问卷的表单
     * @return 用户填写的新的问卷信息，如果用户id不存在则返回null
     */
    QuestionnaireVO updateQuestionnaire(QuestionnaireForm form);

    /**
     * 获取相应用户填写的问卷
     *
     * @param customerId 消费者的id
     * @return 用户填写的新的问卷信息，如果用户id不存在则返回null
     */
    QuestionnaireVO findQuestionnaireByCustomerId(Integer customerId);

    /**
     * 验证用户名
     *
     * @param username 用户名
     * @return false:用户名不符合规范或用户名已存在
     */
    boolean verifyUsername(String username);

    /**
     * 用户点击邮箱中收到的链接确认验证邮箱
     *
     * @param email 邮箱
     * @param code  验证码
     * @return 0:验证成功, 1:邮箱已被验证过, 2:验证错误
     */
    int verifyEmail(String email, String code);

    /**
     * 用户修改密码
     *
     * @param id          用户id
     * @param newPassword 新密码
     * @return -1表示修改失败
     */
    int changePasswordById(int id, String newPassword, String oldPassword);

    /**
     * 修改邮箱
     *
     * @param id       用户id
     * @param newEmail 新邮箱
     * @return -2表示失败
     */
    int changeEmailById(int id, String newEmail);

    Boolean judgeOldCustomer(Customer customer);
}
