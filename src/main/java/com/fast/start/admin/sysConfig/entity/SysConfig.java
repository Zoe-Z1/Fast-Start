package com.fast.start.admin.sysConfig.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.start.common.base.BaseEntity;
import com.fast.start.common.excel.converter.IntegerStatusToStringConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
* @author zoe
* @date 2023/07/29
* @description 系统配置 实体
*/
@Data
@ColumnWidth(20)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_config")
@ApiModel(value = "SysConfig对象", description = "系统配置")
public class SysConfig extends BaseEntity {

    @ExcelIgnore
    @ApiModelProperty("配置域ID")
    @TableField("domain_id")
    private Long domainId;

    @ExcelProperty(value = "系统配置编码")
    @ApiModelProperty("系统配置编码")
    @TableField("code")
    private String code;

    @ExcelProperty(value = "系统配置值")
    @ApiModelProperty("系统配置value值")
    @TableField("value")
    private String value;

    @ExcelProperty(value = "系统配置名称")
    @ApiModelProperty("系统配置名称")
    @TableField("name")
    private String name;

    @ExcelProperty(value = "系统配置状态-正常/禁用", converter = IntegerStatusToStringConvert.class)
    @ApiModelProperty("系统配置状态 1：正常 2：禁用")
    @TableField("status")
    private Integer status;

    @ExcelProperty(value = "备注")
    @ApiModelProperty("备注")
    @TableField("remarks")
    private String remarks;

    @ExcelProperty(value = "排序")
    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;
}
