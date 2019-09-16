package nju.citix.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author DW
 */
@Data
@EqualsAndHashCode(exclude = {"lastLogin"})
public class Customer {
    /**
     * 用户编号
     */
    private Integer customerId;
    /**
     * 用户名:字母或者数字开头；允许字母,数字,下划线和点(.)；5~15位之间
     */
    private String username;
    /**
     * 密码：包含大写字母、小写字母、数字、特殊符号（不是字母，数字，下划线，汉字的字符）的8位以上组合
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 邮箱是否被验证
     */
    private Boolean emailValid;
    /**
     * 余额
     */
    private BigDecimal balance;
    /**
     * 收益
     */
    private BigDecimal bonus;
    /**
     * 注册时间
     */
    private LocalDate joinTime;
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;
    /**
     * 用户问卷对应的id
     */
    private Integer questionId;
    /**
     * 最后登录ip
     */
    private String ip;
    /**
     * 该账号是否被禁止
     */
    private Boolean banned;
}