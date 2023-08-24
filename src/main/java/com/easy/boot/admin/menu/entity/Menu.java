package com.easy.boot.admin.menu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easy.boot.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
* @author zoe
* @date 2023/07/30
* @description 菜单 实体
*/
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("menu")
@ApiModel(value = "Menu对象", description = "菜单")
public class Menu extends BaseEntity {

    @ApiModelProperty("父级菜单ID，为0则代表最上级菜单")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty("菜单图标")
    @TableField("icon")
    private String icon;

    @ApiModelProperty("菜单名称")
    @TableField("label")
    private String label;

    @ApiModelProperty("路由地址")
    @TableField("route_path")
    private String routePath;

    @ApiModelProperty("组件路径")
    @TableField("path")
    private String path;

    @ApiModelProperty("权限字符")
    @TableField("permission")
    private String permission;

    @ApiModelProperty("菜单类型 1：目录  2：菜单 3：按钮")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("菜单状态 1：正常 2：禁用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("显示状态 1：显示 2：隐藏")
    @TableField("show_status")
    private Integer showStatus;

    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("备注")
    @TableField("remarks")
    private String remarks;
}