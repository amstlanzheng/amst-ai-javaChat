package com.amst.ai.contents;

public interface UserContents {

    /**
     * 判断特殊字符的正则
     */
    String VALID_PATTERN = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     *  用户默认权限
     */
    int DEFAULT_ROLE = 0;
    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 邀请码（暂时写死）
     */
    String INVITE_CODE = "Icarus";

}
