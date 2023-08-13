package com.fast.start.common.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zoe
 * @date 2023/8/9
 * @description
 */
@Data
@Builder
@ApiModel(value = "Excel导入返回对象", description = "Excel导入返回对象")
public class ImportVO {

    @ApiModelProperty(value = "导入条数")
    private Integer count;

    @ApiModelProperty(value = "错误条数")
    private Integer errorCount;

    @ApiModelProperty(value = "错误文件下载地址")
    private String errorFilePath;
}
