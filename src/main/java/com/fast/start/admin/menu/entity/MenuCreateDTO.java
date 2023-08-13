package com.fast.start.admin.menu.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @author zoe
* @date 2023/07/30
* @description 菜单 DTO
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Menu对象", description = "菜单")
public class MenuCreateDTO {

    @NotNull(message = "父级菜单不能为空")
    @ApiModelProperty(required = true, value = "父级菜单ID，为0则代表最上级菜单")
    private Long parentId;

    @ApiModelProperty(required = false, value = "菜单图标")
    private String icon;

    @NotBlank(message = "菜单名称不能为空")
    @Length(min = 1, max = 20, message = "菜单名称在{min}-{max}个字符之间")
    @ApiModelProperty(required = true, value = "菜单名称")
    private String label;

    @Length(min = 1, max = 30, message = "路由地址在{min}-{max}个字符之间")
    @ApiModelProperty(required = false, value = "路由地址")
    private String routePath;

    @Length(min = 1, max = 30, message = "组件路径在{min}-{max}个字符之间")
    @ApiModelProperty(required = false, value = "组件路径")
    private String path;

    @Length(min = 1, max = 30, message = "权限字符在{min}-{max}个字符之间")
    @ApiModelProperty(required = false, value = "权限字符")
    private String permission;

    @NotNull(message = "菜单类型不能为空")
    @Range(min = 1, max = 3, message = "菜单类型不正确")
    @ApiModelProperty(required = true, value = "菜单类型 1：目录  2：菜单 3：按钮")
    private Integer type;

    @Range(min = 1, max = 2, message = "菜单状态不正确")
    @ApiModelProperty(required = true, value = "菜单状态 1：正常 2：禁用")
    private Integer status;

    @Range(min = 1, max = 2, message = "显示状态不正确")
    @ApiModelProperty(required = true, value = "显示状态 1：显示 2：隐藏")
    private Integer showStatus;

    @ApiModelProperty(required = false, value = "排序")
    private Integer sort;

    @ApiModelProperty(required = false, value = "备注")
    private String remarks;
}