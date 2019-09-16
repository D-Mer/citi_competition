package nju.citix.dao.manager;

import nju.citix.po.Manager;

import java.util.List;

/**
 * @author jiang hui
 * @date 2019/8/12
 */

public interface ManagerMapper {
    /**
     * 根据主键删除
     *
     * @param managerId 管理员编号
     * @return 删除的数量
     */
    Integer deleteByPrimaryKey(Integer managerId);

    /**
     * 插入新的管理员
     *
     * @param manager 管理员的实体类
     */
    void insert(Manager manager);

    /**
     * 根据id查找管理员
     *
     * @param managerId 管理员id
     * @return 查找到的管理员(或者null)
     */
    Manager selectByPrimaryKey(Integer managerId);

    /**
     * 根据id查找管理员
     *
     * @param username 管理员用户名
     * @return 查找到的管理员(或者null)
     */
    Manager selectByUsername(String username);

    /**
     * 更新管理员信息
     *
     * @param manager 新的管理员信息（允许null值）
     */
    void updateByPrimaryKeySelective(Manager manager);

    /**
     * 根据分页信息查找管理员
     *
     * @param offset  偏移量
     * @param rowsNum 需要的数据的行数
     * @return 查找到的管理员
     */
    List<Manager> selectAllByPage(Integer offset, Integer rowsNum);
}
