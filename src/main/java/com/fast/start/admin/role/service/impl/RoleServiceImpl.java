package com.fast.start.admin.role.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.start.admin.menu.entity.Menu;
import com.fast.start.admin.menu.service.IMenuService;
import com.fast.start.admin.role.entity.Role;
import com.fast.start.admin.role.entity.RoleCreateDTO;
import com.fast.start.admin.role.entity.RoleQuery;
import com.fast.start.admin.role.entity.RoleUpdateDTO;
import com.fast.start.admin.role.mapper.RoleMapper;
import com.fast.start.admin.role.service.IRoleService;
import com.fast.start.admin.roleMenu.service.IRoleMenuService;
import com.fast.start.admin.userRole.entity.UserRole;
import com.fast.start.admin.userRole.service.IUserRoleService;
import com.fast.start.common.base.BaseEntity;
import com.fast.start.common.excel.ImportExcelError;
import com.fast.start.exception.BusinessException;
import com.fast.start.utils.BeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author zoe
* @date 2023/07/30
* @description 角色 服务实现类
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private IRoleMenuService roleMenuService;

    @Resource
    private IMenuService menuService;

    @Override
    public IPage<Role> selectPage(RoleQuery query) {
        Page<Role> page = new Page<>(query.getPageNum(), query.getPageSize());
        return lambdaQuery()
                .like(StrUtil.isNotEmpty(query.getName()), Role::getName, query.getName())
                .like(StrUtil.isNotEmpty(query.getCode()), Role::getCode, query.getCode())
                .eq(Objects.nonNull(query.getStatus()), Role::getStatus, query.getStatus())
                .between(Objects.nonNull(query.getStartTime()) && Objects.nonNull(query.getEndTime()),
                        BaseEntity::getCreateTime, query.getStartTime(), query.getEndTime())
                .orderByAsc(Role::getSort)
                .orderByDesc(BaseEntity::getCreateTime)
                .page(page);
    }

    @Override
    public Role detail(Long id) {
        return getById(id);
    }

    @Override
    public Role getByCode(String code) {
        return lambdaQuery().eq(Role::getCode, code).one();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean create(RoleCreateDTO dto) {
        Role role = this.getByCode(dto.getCode());
        if (Objects.nonNull(role)) {
            throw new BusinessException("角色编码已存在");
        }
        Role entity = BeanUtil.copyBean(dto, Role.class);
        boolean status = save(entity);
        if (status) {
            Menu menu = menuService.getRoot();
            if (CollUtil.isNotEmpty(dto.getMenuIds()) && !dto.getMenuIds().contains(menu.getId())) {
                throw new BusinessException("必须勾选最上级菜单");
            }
            roleMenuService.batchCreate(dto.getMenuIds(), entity.getId());
        }
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateById(RoleUpdateDTO dto) {
        Role role = this.getByCode(dto.getCode());
        if (Objects.nonNull(role) && !dto.getId().equals(role.getId())) {
            throw new BusinessException("角色编码已存在");
        }
        // 必须要有最上级菜单
        Menu menu = menuService.getRoot();
        if (CollUtil.isNotEmpty(dto.getMenuIds()) && !dto.getMenuIds().contains(menu.getId())) {
            throw new BusinessException("必须勾选最上级菜单");
        }
        // 删除菜单后新增
        roleMenuService.deleteByRoleId(dto.getId());
        roleMenuService.batchCreate(dto.getMenuIds(), dto.getId());
        Role entity = BeanUtil.copyBean(dto, Role.class);
        return updateById(entity);
    }

    @Override
    public Boolean deleteById(Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        List<UserRole> userRoles = userRoleService.selectListByRoleIds(ids);
        if (CollUtil.isNotEmpty(userRoles)) {
            throw new BusinessException("已有用户绑定选中的角色，不允许删除");
        }
        return removeById(id);
    }

    @Override
    public Boolean deleteBatchByIds(List<Long> ids) {
        List<UserRole> userRoles = userRoleService.selectListByRoleIds(ids);
        if (CollUtil.isNotEmpty(userRoles)) {
            throw new BusinessException("已有用户绑定选中的角色，不允许删除");
        }
        return removeBatchByIds(ids);
    }

    @Override
    public List<String> selectCodesInRoleIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<Role> list = lambdaQuery().in(BaseEntity::getId, ids).list();
        return list.stream().map(Role::getCode).distinct().collect(Collectors.toList());
    }

    @Override
    public void importExcel(List<Role> list, List<Role> errorList, List<ImportExcelError> errors) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<String> codes = list.stream().map(Role::getCode).distinct().collect(Collectors.toList());
        List<Role> roles = lambdaQuery().in(Role::getCode, codes).list();
        codes = roles.stream().map(Role::getCode).distinct().collect(Collectors.toList());
        // 去除表头 行数从1起算
        int rowIndex = 1;
        Iterator<Role> iterator = list.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            boolean isError = false;
            ImportExcelError.ImportExcelErrorBuilder builder = ImportExcelError.builder();
            if (StrUtil.isEmpty(role.getCode())) {
                isError = true;
                builder.columnIndex(0).rowIndex(rowIndex).msg("角色编码不能为空");
                errors.add(builder.build());
            } else if (role.getCode().length() < 1 || role.getCode().length() > 20) {
                isError = true;
                builder.columnIndex(1).rowIndex(rowIndex).msg("角色编码在1-20个字符之间");
                errors.add(builder.build());
            }
            if (codes.contains(role.getCode())) {
                isError = true;
                builder.columnIndex(0).rowIndex(rowIndex).msg("角色编码已存在");
                errors.add(builder.build());
            }
            if (StrUtil.isEmpty(role.getName())) {
                isError = true;
                builder.columnIndex(1).rowIndex(rowIndex).msg("角色名称不能为空");
                errors.add(builder.build());
            } else if (role.getName().length() < 1 || role.getName().length() > 20) {
                isError = true;
                builder.columnIndex(1).rowIndex(rowIndex).msg("角色名称在1-20个字符之间");
                errors.add(builder.build());
            }
            if (role.getStatus() == null) {
                isError = true;
                builder.columnIndex(2).rowIndex(rowIndex).msg("角色状态不能为空");
                errors.add(builder.build());
            }
            // 这一行有错误，行数增加，错误数据加到list，删除原list的数据
            if (isError) {
                rowIndex++;
                errorList.add(role);
                iterator.remove();
            } else {
                // 没有错误，会进行新增，加进去不存在的，防止后续存在
                codes.add(role.getCode());
            }
        }
        saveBatch(list);
    }

}
