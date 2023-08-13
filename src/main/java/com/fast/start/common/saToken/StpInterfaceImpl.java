package com.fast.start.common.saToken;

import cn.dev33.satoken.stp.StpInterface;
import com.fast.start.admin.menu.service.IMenuService;
import com.fast.start.admin.role.service.IRoleService;
import com.fast.start.admin.roleMenu.service.IRoleMenuService;
import com.fast.start.admin.userRole.service.IUserRoleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zoe
 * @date 2023/7/30
 * @description sa-token自定义权限实现
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private IRoleMenuService roleMenuService;

    @Resource
    private IMenuService menuService;

    @Resource
    private IRoleService roleService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<Long> roleIds = userRoleService.selectListByUserId(Long.valueOf(String.valueOf(loginId)));
        List<Long> menuIds = roleMenuService.selectMenuIdsByRoleIds(roleIds);
        return menuService.selectPermissionsByIds(menuIds);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<Long> roleIds = userRoleService.selectListByUserId(Long.valueOf(String.valueOf(loginId)));
        return roleService.selectCodesInRoleIds(roleIds);
    }
}
