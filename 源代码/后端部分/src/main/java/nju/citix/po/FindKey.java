package nju.citix.po;

import lombok.Data;

/**
 * @author DW
 */
@Data
public class FindKey {
    /**
     * 找回编号
     */
    private Integer findId;
    /**
     * 对应用户邮箱
     */
    private String email;
    /**
     * 发出找回请求的时间
     */
    private String requestTime;
    /**
     * 是否已经被使用
     */
    private Boolean used;
}