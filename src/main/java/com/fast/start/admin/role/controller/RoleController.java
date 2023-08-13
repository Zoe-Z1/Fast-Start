package com.fast.start.admin.role.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.start.admin.operationLog.enums.OperateTypeEnum;
import com.fast.start.admin.post.entity.Post;
import com.fast.start.admin.role.entity.*;
import com.fast.start.admin.role.service.IRoleService;
import com.fast.start.admin.userRole.service.IUserRoleService;
import com.fast.start.common.base.BaseController;
import com.fast.start.common.base.Result;
import com.fast.start.common.excel.ImportExcelError;
import com.fast.start.common.excel.ImportVO;
import com.fast.start.common.excel.UploadDTO;
import com.fast.start.common.excel.handler.ImportErrorCellWriteHandler;
import com.fast.start.common.log.FastLog;
import com.fast.start.common.properties.FastFile;
import com.fast.start.exception.FileException;
import com.fast.start.utils.FileUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zoe
 * @date 2023/07/30
 * @description 角色 前端控制器
 */
@Slf4j
@Api(tags = "角色接口")
@RestController
@RequestMapping("/admin/role")
public class RoleController extends BaseController {

    @Resource
    private IRoleService roleService;

    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private FastFile fastFile;

    @Value("${mybatis-plus.global-config.max-limit}")
    private Integer maxLimit;


    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取角色列表")
    @FastLog(module = "获取角色列表", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/page")
    public Result<IPage<Role>> page(@Validated RoleQuery query) {
        return Result.success(roleService.selectPage(query));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取角色详情")
    @FastLog(module = "获取角色详情", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/detail/{id}")
    public Result<Role> detail(@PathVariable Long id) {
        return Result.success(roleService.detail(id));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "创建角色")
    @FastLog(module = "创建角色", operateType = OperateTypeEnum.INSERT)
    @PostMapping(value = "/create")
    public Result create(@Validated @RequestBody RoleCreateDTO dto) {
        return Result.r(roleService.create(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "角色分配用户")
    @FastLog(module = "角色分配用户", operateType = OperateTypeEnum.INSERT)
    @PostMapping(value = "/roleAllotUser")
    public Result roleAllotUser(@Validated @RequestBody RoleAllotUserDTO dto) {
        return Result.r(userRoleService.roleAllotUser(dto.getUserIds(), dto.getRoleId()));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "编辑角色")
    @FastLog(module = "编辑角色", operateType = OperateTypeEnum.UPDATE)
    @PostMapping(value = "/update")
    public Result update(@Validated @RequestBody RoleUpdateDTO dto) {
        return Result.r(roleService.updateById(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "删除角色")
    @FastLog(module = "删除角色", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.r(roleService.deleteById(id));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "批量删除角色")
    @FastLog(module = "批量删除角色", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/batchDel/{ids}")
    public Result batchDel(@PathVariable List<Long> ids) {
        return Result.r(roleService.deleteBatchByIds(ids));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "导入角色")
    @FastLog(module = "导入角色", operateType = OperateTypeEnum.IMPORT)
    @PostMapping("/import")
    public Result<ImportVO> importExcel(UploadDTO dto) {
        Assert.notNull(dto.getFile(), "文件不能为空");
        try {
            List<Role> list = EasyExcel.read(dto.getFile().getInputStream())
                    .head(Role.class)
                    .excelType(FileUtil.getExcelType(dto.getFile()))
                    .sheet()
                    .doReadSync();
            List<ImportExcelError> errors = new ArrayList<>();
            List<Role> errorList = new ArrayList<>();
            // 导入Excel处理
            roleService.importExcel(list, errorList, errors);
            String filePath = "";
            if (!errorList.isEmpty()) {
                // 将错误数据写到Excel文件
                filePath = FileUtil.getFullPath(fastFile.getFilePath(), "角色导入错误信息");
                EasyExcel.write(filePath).head(Post.class)
                        .sheet().registerWriteHandler(new ImportErrorCellWriteHandler(errors))
                        .doWrite(errorList);
                filePath = FileUtil.getMapPath(filePath, fastFile.getFilePath(), fastFile.getFileMapPath());
            }
            ImportVO importVO = ImportVO.builder()
                    .count(list.size())
                    .errorCount(errorList.size())
                    .errorFilePath(filePath)
                    .build();
            return Result.success(importVO);
        } catch (IOException e) {
            log.error("导入Excel失败 e -> ", e);
            throw new FileException("导入Excel失败");
        }
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "导出角色")
    @FastLog(module = "导出角色", operateType = OperateTypeEnum.EXPORT)
    @PostMapping("/export")
    public void exportExcel(@Validated @RequestBody RoleQuery query) {
        String filePath = FileUtil.getFullPath(fastFile.getExcelPath(), "角色");
        query.setPageNum(1);
        query.setPageSize(maxLimit);
        ExcelWriter build = EasyExcel.write(filePath, Role.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("角色").build();
        while (true) {
            IPage<Role> page = roleService.selectPage(query);
            build.write(page.getRecords(), writeSheet);
            if (page.getCurrent() >= page.getPages()) {
                break;
            }
            query.setPageNum(query.getPageNum() + 1);
        }
        build.finish();
        try {
            FileUtil.downloadAndDelete(filePath, response);
        } catch (IOException e) {
            log.error("导出Excel失败 e -> ", e);
            throw new FileException("导出Excel失败");
        }
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "下载角色导入模板")
    @FastLog(module = "下载角色导入模板", operateType = OperateTypeEnum.DOWNLOAD)
    @PostMapping("/download")
    public void downloadTemplate(HttpServletResponse response) {
        String filePath = FileUtil.getFullPath(fastFile.getExcelPath(), "角色导入模板");
        EasyExcel.write(filePath, Role.class)
                .excludeColumnFieldNames(Collections.singletonList("createTime"))
                .sheet("角色导入模板")
                .doWrite(new ArrayList<>());
        try {
            FileUtil.downloadAndDelete(filePath, response);
        } catch (IOException e) {
            log.error("下载角色导入模板失败 e -> ", e);
            throw new FileException("下载角色导入模板失败");
        }
    }
}