package com.amst.ai.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SysUserVO {
    /**
     * id
     */
    private String id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private String gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 盐值
     */
    private String salt;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */
    private String userStatus;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private String userRole;

    /**
     * 邀请编号
     */
    private String planetCode;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     *
     */
    private String updateTime;



    private static final long serialVersionUID = 1L;

}
