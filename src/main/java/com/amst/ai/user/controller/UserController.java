package com.amst.ai.user.controller;

import com.amst.ai.common.enums.ErrorCode;
import com.amst.ai.common.exception.BusinessException;
import com.amst.ai.common.result.Result;
import com.amst.ai.common.utils.DataUtils;
import com.amst.ai.user.contents.UserContents;
import com.amst.ai.user.model.entity.SysUser;
import com.amst.ai.user.model.query.UserLoginQuery;
import com.amst.ai.user.model.query.UserRegisterQuery;
import com.amst.ai.user.model.vo.SysUserVO;
import com.amst.ai.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lanzhs
 */
@RestController
@RequestMapping("api/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {


    public final SysUserService sysUserService;

    @PostMapping("/register")
    public Result<Long> register(@RequestBody UserRegisterQuery userQuery) {
        //判空
        if (userQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能传入空数据");
        }
        log.info("register: {}", userQuery);
        String userAccount = userQuery.getUserAccount();
        String userPassword = userQuery.getUserPassword();
        String checkPassword = userQuery.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "传入的账号，密码不能为空");
        }
        long result = sysUserService.userRegister(userQuery.getUserAccount(), userQuery.getUserPassword(), userQuery.getCheckPassword(), userQuery.getPlanetCode());
        return Result.ok(result);
    }

    @PostMapping("/login")
    public Result<SysUser> userLogin(@RequestBody UserLoginQuery userQuery, HttpServletRequest request) {
        //判空
        if (ObjectUtils.isEmpty(userQuery)){
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数不能为空");
        }
        //校验
        if (StringUtils.isAnyBlank(userQuery.getUserAccount(), userQuery.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        SysUser sysUser = sysUserService.userLogin(userQuery.getUserAccount(), userQuery.getUserPassword(), request);
        return Result.ok(sysUser);

    }

    /**
     * 查询用户
     */
    @GetMapping("/search")
    public Result<List<SysUserVO>> query(@RequestParam(required = false) String username, HttpServletRequest  request) {

        if (isNotAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "非管理员无权限查询");
        }
        List<SysUser> userList = sysUserService.list(new QueryWrapper<SysUser>().like(StringUtils.isNotBlank(username), "username", username));

        userList.forEach(sysUser -> sysUser.setUserPassword(null));
        List<SysUserVO> sysUserVOList = userList.stream().map(sysUser -> {
            SysUserVO sysUserVO = new SysUserVO();
            sysUserVO.setId(sysUser.getId() + "");
            sysUserVO.setUsername(sysUser.getUsername());
            sysUserVO.setUserAccount(sysUser.getUserAccount());
            sysUserVO.setAvatarUrl(sysUser.getAvatarUrl());
            sysUserVO.setGender(sysUser.getGender() + "");
            sysUserVO.setPhone(sysUser.getPhone());
            sysUserVO.setPlanetCode(sysUser.getPlanetCode());
            sysUserVO.setCreateTime(DataUtils.date2Str(sysUser.getCreateTime()));
            sysUserVO.setUserRole(sysUser.getUserRole() + "");
            return sysUserVO;
        }).toList();

        return Result.ok(sysUserVOList);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestBody Long id, HttpServletRequest request) {

        if (isNotAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "非管理员无权限删除");
        }
        //判空
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能传入空数据");
        }
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能传入无效数据");
        }

        sysUserService.removeById(id);
        return Result.ok();
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/current")
    public Result<List<SysUserVO>> getCurrentUser(HttpServletRequest request) {
        SysUser suser = (SysUser) request.getSession().getAttribute(UserContents.USER_LOGIN_STATE);
        if (suser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        List<SysUser> userList = sysUserService.list();
        List<SysUserVO> userVOList = userList.stream().map(sysUser -> {
            SysUserVO sysUserVO = new SysUserVO();
            BeanUtils.copyProperties(sysUser, sysUserVO);
            sysUserVO.setId(sysUser.getId() + "");
            sysUserVO.setUserRole(sysUser.getUserRole() + "");
            sysUserVO.setGender(sysUser.getGender() + "");
            return sysUserVO;
        }).toList();
        return Result.ok(userVOList);
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        sysUserService.userLogout(request);
        return Result.ok();
    }


    /**
     * 是否管理员
     */

    private boolean isNotAdmin(HttpServletRequest  request) {
        Object attribute = request.getSession().getAttribute(UserContents.USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty( attribute)) {
            //未登录
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        SysUser sysUser = (SysUser) attribute;
        Integer userRole = sysUser.getUserRole();
        return userRole != UserContents.ADMIN_ROLE;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/currentUser")
    public Result<SysUserVO> currentUser(HttpServletRequest request) {
        SysUser sysUser = (SysUser) request.getSession().getAttribute(UserContents.USER_LOGIN_STATE);
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        SysUserVO sysUserVO = new SysUserVO();
        BeanUtils.copyProperties(sysUser, sysUserVO);
        sysUserVO.setId(sysUser.getId() + "");
        sysUserVO.setUserRole(sysUser.getUserRole() + "");
        sysUserVO.setGender(sysUser.getGender() + "");

        return Result.ok(sysUserVO);
    }


    /**
     * 修改
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody SysUser sysUser, HttpServletRequest request) {
        if (isNotAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "非管理员无权限修改");
        }
        sysUserService.updateUser(sysUser);
        return Result.ok("修改成功");

    }


}
