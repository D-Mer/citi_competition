package nju.citix.serviceImpl.manager;

import nju.citix.dao.manager.ManagerMapper;
import nju.citix.po.Customer;
import nju.citix.po.Manager;
import nju.citix.service.customer.CustomerForService;
import nju.citix.service.manager.ManagerService;
import nju.citix.vo.ManagerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiang hui
 * @date 2019/8/12
 */

@Service
public class ManagerServiceImpl implements ManagerService {

    @Resource
    private ManagerMapper managerMapper;
    @Autowired
    private CustomerForService customerForService;

    /**
     * 管理员的登录验证
     *
     * @param username 管理员用户名
     * @param password 管理员密码
     * @return 如果验证成功则返回对应的管理员对象，失败则返回null
     */
    @Override
    public Manager login(String username, String password) {
        Manager manager = managerMapper.selectByUsername(username);
        if (manager != null && password.equals(manager.getPassword()) && !manager.getBanned()) {
            manager.setLastLogin(LocalDateTime.now());
            managerMapper.updateByPrimaryKeySelective(manager);
            return manager;
        } else {
            return null;
        }
    }

    /**
     * 增加管理员
     *
     * @param managerForm 前端传来的管理员信息表单
     * @return 新增的管理员对象，包括生成的id
     */
    @Override
    @Transactional(rollbackFor = DuplicateKeyException.class)
    public Manager addManager(ManagerForm managerForm) {
        Manager manager = new Manager();
        manager.setUsername(managerForm.getUsername());
        manager.setPassword(managerForm.getPassword());
        manager.setBanned(managerForm.getBanned() == null ? false : managerForm.getBanned());
        managerMapper.insert(manager);
        return manager;
    }

    /**
     * 超级管理员修改管理员密码
     *
     * @param managerId   管理员ID
     * @param newPassword 新管理员密码
     * @return 如果用户名存在则修改后返回true，不存在则返回false
     */
    @Override
    public boolean updateManagerPassword(Integer managerId, String newPassword) {
        Manager manager = findManagerById(managerId);
        if (manager == null) {
            return false;
        }
        manager.setPassword(newPassword);
        managerMapper.updateByPrimaryKeySelective(manager);
        return true;
    }

    /**
     * 管理员修改自己的密码
     *
     * @param managerId   管理员ID
     * @param password    旧密码
     * @param newPassword 新密码
     * @return 用户ID由前端传入，不需要用户输入，如果原密码输入正确则修改后返回true，错误则返回false
     */
    @Override
    public boolean updateOwnPassword(Integer managerId, String password, String newPassword) {
        Manager manager = findManagerById(managerId);
        if (!password.equals(manager.getPassword())) {
            return false;
        }
        manager.setPassword(newPassword);
        managerMapper.updateByPrimaryKeySelective(manager);
        return true;
    }

    /**
     * 禁用管理员
     *
     * @param managerId 管理员ID
     * @return 若该用户未被禁用，则修改后返回true，否则返回false
     */
    @Override
    public boolean banManager(Integer managerId) {
        Manager manager = findManagerById(managerId);
        if (manager == null) {
            return false;
        }
        manager.setBanned(true);
        managerMapper.updateByPrimaryKeySelective(manager);
        return true;
    }

    /**
     * 恢复管理员
     *
     * @param managerId 管理员ID
     * @return 若该用户已被禁用，则修改后返回true，否则返回false
     */
    @Override
    public boolean unbanManager(Integer managerId) {
        Manager manager = findManagerById(managerId);
        if (manager == null) {
            return false;
        }
        manager.setBanned(false);
        managerMapper.updateByPrimaryKeySelective(manager);
        return true;
    }

    /**
     * 根据id查找管理员
     *
     * @param managerId 要查找的管理员的id
     * @return 需要查找的管理员对象，若数据库中没有则返回null
     */
    @Override
    public Manager findManagerById(Integer managerId) {
        return managerMapper.selectByPrimaryKey(managerId);
    }

    @Override
    public List<Manager> viewAllManagers(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum <= 0 || pageSize == null || pageSize <= 0) {
            return null;
        }
        Integer offset = (pageNum - 1) * pageSize;
        return managerMapper.selectAllByPage(offset, pageSize);
    }

    @Override
    public Customer viewCustomerInfo(Integer customerId) {
        return customerForService.viewCustomerInfo(customerId);
    }

    @Override
    public boolean banUser(Integer customerId) {
        return customerForService.banUser(customerId);
    }

    @Override
    public boolean releaseUser(Integer customerId) {
        return customerForService.releaseUser(customerId);
    }

    @Override
    public List<Customer> viewAllCustomers(Integer pageNum, Integer pageSize) {
        return customerForService.viewAllCustomers(pageNum, pageSize);
    }
}
