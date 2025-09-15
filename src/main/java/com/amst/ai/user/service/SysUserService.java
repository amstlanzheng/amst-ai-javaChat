package com.amst.ai.user.service;

import com.amst.ai.user.model.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author amstl
* @description 针对表【sys_user(用户)】的数据库操作Service
* @createDate 2025-08-19 16:41:03
*/
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);




    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    SysUser userLogin(String userAccount, String userPassword, HttpServletRequest request);



    /**
     * 获取当前登录用户
     * @param request 请求
     */
    Integer userLogout(HttpServletRequest request);

    /**
     * 更新用户信息
     * @param sysUser 用户信息
     */
    void updateUser(SysUser sysUser);
}
