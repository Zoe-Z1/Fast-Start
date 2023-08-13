package com.fast.start.admin.roleMenu.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.start.admin.roleMenu.entity.RoleMenu;
import com.fast.start.admin.roleMenu.entity.RoleMenuQuery;
import com.fast.start.admin.roleMenu.mapper.RoleMenuMapper;
import com.fast.start.admin.roleMenu.service.IRoleMenuService;
import com.fast.start.common.base.BaseEntity;
import lombok.var;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author zoe
* @date 2023/07/30
* @description 角色菜单关联 服务实现类
*/
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Override
    public List<RoleMenu> selectList(RoleMenuQuery query) {
        return lambdaQuery()
                .eq(Objects.nonNull(query.getMenuId()), RoleMenu::getMenuId, query.getMenuId())
                .eq(Objects.nonNull(query.getRoleId()), RoleMenu::getRoleId, query.getRoleId())
                .orderByDesc(BaseEntity::getCreateTime)
                .list();
    }

    @Override
    public List<Long> selectMenuIdsByRoleIds(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        var list = lambdaQuery().in(RoleMenu::getRoleId, roleIds).list();
        return list.stream().map(RoleMenu::getMenuId).distinct().collect(Collectors.toList());
    }

    @Override
    public Boolean batchCreate(List<Long> ids, Long roleId) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        List<RoleMenu> list = ids.stream()
                .map(id -> RoleMenu.builder().menuId(id).roleId(roleId).build())
                .collect(Collectors.toList());
        return saveBatch(list);
    }


    @Override
    public Boolean deleteByRoleId(Long roleId) {
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        return remove(queryWrapper);
    }

}
