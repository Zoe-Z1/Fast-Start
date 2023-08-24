package com.easy.boot.admin.login.service;


import com.easy.boot.admin.user.entity.AdminUser;
import com.easy.boot.admin.login.entity.LoginDTO;

/**
 * @author zoe
 * @date 2023/7/23
 * @description
 */
public interface AdminLoginService {

    /**
     * 用户登录
     * @param dto
     * @return
     */
    AdminUser login(LoginDTO dto);

    /**
     * 检查登录状态
     * @return
     */
    void checkLogin();
}