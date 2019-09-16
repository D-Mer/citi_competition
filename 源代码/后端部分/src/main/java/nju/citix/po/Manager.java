package nju.citix.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiang hui
 * @data 2019/8/12
 */

@Data
public class Manager {

    /**
     * 管理员id
     */
    private Integer managerId;

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

    /**
     * 该账号上次登录时间
     */
    private LocalDateTime lastLogin;
}
