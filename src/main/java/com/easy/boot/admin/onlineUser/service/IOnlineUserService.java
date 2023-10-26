package com.easy.boot.admin.onlineUser.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easy.boot.admin.onlineUser.entity.OnlineUser;
import com.easy.boot.admin.onlineUser.entity.OnlineUserQuery;

/**
* @author zoe
* @date 2023/08/02
* @description 在线用户 服务类
*/
public interface IOnlineUserService extends IService<OnlineUser> {

    /**
    * 查询在线用户
    * @param query
    * @return
    */
    IPage<OnlineUser> selectPage(OnlineUserQuery query);

    /**
     * 根据token删除在线用户
     * @param token
     */
    Boolean deleteByToken(String token);

    /**
     * 更新用户在线状态
     */
    void updateIsOnline();

    /**
     * 根据在线用户ID下线用户
     * @param id
     * @return
     */
    Boolean kickoutById(Long id);

}