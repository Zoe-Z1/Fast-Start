package com.easy.boot.admin.sysConfig.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

/**
* @author zoe
* @date 2023/07/29
* @description 系统配置 DTO
*/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "SysConfig对象", description = "系统配置")
public class SysConfigUpdateDTO extends SysConfigCreateDTO {

    @NotNull(message = "系统配置ID不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, title = "系统配置ID")
    private Long id;

}
