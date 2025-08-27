package com.amst.ai.model.query;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: amstlan
 * @date: 2025/7/20 15:01
 * @version: 1.0
 */

@Data
public class UserLoginQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
}
