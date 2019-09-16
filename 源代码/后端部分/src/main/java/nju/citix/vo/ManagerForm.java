package nju.citix.vo;

import lombok.Data;

/**
 * @author jiang hui
 * @date 2019/8/12
 */
@Data
public class ManagerForm {
    /**
     * 管理员用户名
     */
    private String username;

    /**
     * 管理员密码
     */
    private String password;

    /**
     * 该管理员账号是否被禁用，默认为false，即不禁用
     */
    private Boolean banned;

}
