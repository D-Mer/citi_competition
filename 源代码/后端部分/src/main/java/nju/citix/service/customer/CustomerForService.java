package nju.citix.service.customer;

import nju.citix.po.Customer;

import java.util.List;


/**
 * @author zhm
 * @date 2019/8/22
 */
public interface CustomerForService {

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
