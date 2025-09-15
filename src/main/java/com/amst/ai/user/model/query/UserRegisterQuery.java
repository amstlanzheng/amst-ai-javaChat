package com.amst.ai.user.model.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: amstlan
 * @date: 2025/7/20 15:01
 * @version: 1.0
 */

@Data
public class UserRegisterQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * 邀请码
     */
    private String planetCode;
}
