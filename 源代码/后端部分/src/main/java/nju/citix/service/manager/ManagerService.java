package nju.citix.service.manager;


import nju.citix.po.Customer;
import nju.citix.po.Manager;
import nju.citix.vo.ManagerForm;

import java.util.List;

/**
 * @author jiang hui
 * @date 2019/8/12
 */

public interface ManagerService {

    /**
     * 管理员的登录验证
     *
     * @param username 管理员用户名
     * @param password 管理员密码
     * @return 如果验证成功则返回对应的管理员对象，失败则返回null
     */
    Manager login(String username, String password);

    /**
     * 增加管理员
     *
     * @param managerForm 前端传来的管理员信息表单
     * @return 新增的管理员对象，包括生成的id
     */
    Manager addManager(ManagerForm managerForm);

    /**
     * 超级管理员修改管理员密码
     *
     * @param managerId   管理员ID
     * @param newPassword 新管理员密码
     * @return 如果用户名存在则修改后返回true，不存在则返回false
     */
    boolean updateManagerPassword(Integer managerId, String newPassword);

    /**
     * 管理员修改自己的密码
     *
     * @param managerId   管理员ID
     * @param password    旧密码
     * @param newPassword 新密码
     * @return 用户ID由前端传入，不需要用户输入，如果原密码输入正确则修改后返回true，错误则返回false
     */
    boolean updateOwnPassword(Integer managerId, String password, String newPassword);

    /**
     * 禁用管理员
     *
     * @param managerId 管理员ID
     * @return 若该用户未被禁用，则修改后返回true，否则返回false
     */
    boolean banManager(Integer managerId);

    /**
     * 恢复管理员
     *
     * @param managerId 管理员ID
     * @return 若该用户已被禁用，则修改后返回true，否则返回false
     */
    boolean unbanManager(Integer managerId);

    Manager findManagerById(Integer managerId);

    /**
     * 主管理员查看所有管理员
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 每页中包含的管理员信息的数量，大于等于1
     * @return 指定页的所有管理员信息
     */
    List<Manager> viewAllManagers(Integer pageNum, Integer pageSize);

    /**
     * 查看某个消费者的信息
     *
     * @param customerId 消费者id
     * @return 消费者的信息
     */
    Customer viewCustomerInfo(Integer customerId);

    /**
     * 禁用消费者
     *
     * @param customerId 消费者id
     * @return false表示禁用失败
     */
    boolean banUser(Integer customerId);

    /**
     * 恢复消费者
     *
     * @param customerId 消费者id
     * @return false表示恢复失败
     */
    boolean releaseUser(Integer customerId);

    /**
     * 管理员查看所有消费者
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 每页中包含的管理员信息的数量，大于等于1
     * @return 指定页的所有消费者信息
     */
    List<Customer> viewAllCustomers(Integer pageNum, Integer pageSize);
}
