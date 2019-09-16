package nju.citix.vo;

import lombok.Data;
import nju.citix.controller.customer.CustomerController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author DW
 * @date 2019/8/12
 */
@Data
public class UserForm {
    /**
     * 用户名:字母或者数字开头；允许字母,数字,下划线和点(.)；5~15位之间.
     */
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9_.]{4,14}$", message = CustomerController.INVALID_NAME)
    private String username;
    /**
     * 密码：包含大写字母、小写字母、数字、特殊符号（不是字母，数字，下划线，汉字的字符）的8位以上组合
     */
    @Pattern(regexp = "^(?![A-Za-z0-9_]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$).{8,20}$", message = CustomerController.INVALID_PASSWORD)
    private String password;
    /**
     * 邮箱
     */
    @NotNull
    @Email(message = "邮箱格式错误")
    private String email;
}

