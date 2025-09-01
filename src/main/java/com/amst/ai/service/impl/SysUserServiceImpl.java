package com.amst.ai.service.impl;

import com.amst.ai.common.ErrorCode;
import com.amst.ai.contents.UserContents;
import com.amst.ai.exception.BusinessException;
import com.amst.ai.mapper.SysUserMapper;
import com.amst.ai.model.entity.SysUser;
import com.amst.ai.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;

import static com.amst.ai.contents.UserContents.USER_LOGIN_STATE;

/**
 * @author amstl
 * @description 针对表【sys_user(用户)】的数据库操作Service实现
 * @createDate 2025-08-19 16:41:03
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //校验，所有数据不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //账号不能小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        //密码不能小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 邀请码不能过短
        if (planetCode.length() < 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邀请码过短");
        }
        //账号不能包含特殊字符
        boolean matches = userAccount.matches(UserContents.VALID_PATTERN);
        if (matches) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        //两个密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不一致");
        }

        //校验邀请码
        if (!planetCode.equals(UserContents.INVITE_CODE)){
            //没有特殊邀请码，查询用户邀请码
            long planet_code = this.count(new QueryWrapper<SysUser>().eq("planet_code", planetCode));
            if (planet_code <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邀请码错误");
            }
        }


        //账号不能重复
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        //密码加密
        String salt = UUID.randomUUID().toString();
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());

        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(userAccount);
        sysUser.setUserPassword(encryptPassword);
        sysUser.setPlanetCode(UUID.randomUUID().toString());
        sysUser.setUserRole(0);
        sysUser.setSalt(salt);
        boolean save = this.save(sysUser);
        if (!save) {
            throw new BusinessException(ErrorCode.ADD_SYSTEM_USER_ERROR, "注册用户失败");
        }
        return sysUser.getId();
    }

    @Override
    public SysUser userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验，所有数据不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //账号不能小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        //密码不能小于8位
        if (userPassword.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //账号不能包含特殊字符
        boolean matches = userAccount.matches(UserContents.VALID_PATTERN);
        if (matches) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        SysUser sysUser = this.getOne(queryWrapper);
        if (sysUser == null) {
            log.error("用户不存在");
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((sysUser.getSalt() + userPassword).getBytes());
        if (!sysUser.getUserPassword().equals(encryptPassword)) {
            log.error("密码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        SysUser safeUser = this.getById(sysUser.getId());
        safeUser.setUserPassword(null);
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);
        log.info("Session ID: {}", request.getSession().getId());
        log.info("Session attributes: {}", request.getSession().getAttributeNames());
        return safeUser;
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {

        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public void updateUser(SysUser sysUser) {
        SysUser user = this.getById(sysUser);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        String userAccount = sysUser.getUserAccount();
        String userPassword = sysUser.getUserPassword();

        if (StringUtils.isBlank(userAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号不能为空");
        }
        //账号不能小于4位

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }

        //账号不能包含特殊字符
        boolean matches = userAccount.matches(UserContents.VALID_PATTERN);
        if (matches) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }

        if (StringUtils.isNotBlank(userPassword)){
            //密码不能小于8位
            if (userPassword.length() < 8) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
            }
            //密码加密
            String encryptPassword = DigestUtils.md5DigestAsHex((user.getSalt() + sysUser.getUserPassword()).getBytes());
            sysUser.setUserPassword(encryptPassword);

        }


        //账号不能重复
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount).ne("id", user.getId());
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }



        this.updateById(sysUser);
    }
}




