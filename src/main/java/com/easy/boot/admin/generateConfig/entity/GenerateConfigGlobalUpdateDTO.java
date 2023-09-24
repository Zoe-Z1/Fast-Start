package com.easy.boot.admin.generateConfig.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * @author zoe
 * @date 2023/09/10
 * @description 代码生成参数配置编辑实体
 */
@ApiModel(value = "代码生成全局参数配置编辑实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode
public class GenerateConfigGlobalUpdateDTO {

    @ApiModelProperty("后端模块名称")
    private String moduleName;

    @ApiModelProperty("前端模块名称")
    private String uiModuleName;

    @ApiModelProperty("包名")
    private String packageName;

    @ApiModelProperty("RequestMapping 路径前缀")
    private String requestMappingPrefix;

    @ApiModelProperty("生成代码路径")
    private String outputPath;

    @ApiModelProperty("生成完代码后是否打开目录	0：打开 1：不打开")
    private Integer isOpen;

    @ApiModelProperty("作者")
    private String author;

    @ApiModelProperty("生成导入 0：生成 1：不生成")
    private Integer enableImport;

    @ApiModelProperty("生成导出 0：生成 1：不生成")
    private Integer enableExport;

    @ApiModelProperty("生成log注解 0：生成 1：不生成")
    private Integer enableLog;

    @ApiModelProperty("开启Builder模式 0：开启 1：不开启")
    private Integer enableBuilder;

    @ApiModelProperty("生成模板配置")
    private GenerateTemplate templateJson;

    @ApiModelProperty("过滤表前缀 多个用,分隔")
    private String excludeTablePrefix;

    @ApiModelProperty("过滤表后缀 多个用,分隔")
    private String excludeTableSuffix;

    @ApiModelProperty("过滤实体类属性 多个用,分隔")
    private String excludeField;

}