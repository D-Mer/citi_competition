package nju.citix.dao.customer;

import nju.citix.po.FindKey;
import org.apache.ibatis.annotations.Param;

/**
 * @author DW
 */
public interface FindKeyMapper {
    /**
     * 插入新的查找记录
     *
     * @param record 新记录情况
     */
    int insert(FindKey record);

    /**
     * 根据id获取对应的记录
     */
    int updateUsedByFindId(@Param("findId") Integer findId);

    /**
     * 根据邮箱获取对应的未过期记录
     */
    FindKey selectByEmail(@Param("email") String email);
}