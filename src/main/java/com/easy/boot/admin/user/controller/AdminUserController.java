package com.easy.boot.admin.user.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easy.boot.admin.operationLog.enums.OperateTypeEnum;
import com.easy.boot.admin.user.entity.*;
import com.easy.boot.admin.user.service.AdminUserService;
import com.easy.boot.common.base.BaseController;
import com.easy.boot.common.base.Result;
import com.easy.boot.common.excel.ImportExcelError;
import com.easy.boot.common.excel.ImportVO;
import com.easy.boot.common.excel.UploadDTO;
import com.easy.boot.common.excel.handler.ImportErrorCellWriteHandler;
import com.easy.boot.common.log.EasyLog;
import com.easy.boot.common.properties.EasyFile;
import com.easy.boot.exception.FileException;
import com.easy.boot.utils.FileUtil;
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
 * @date 2023/7/21
 * @description
 */
@Slf4j
@Api(tags = "用户接口")
@RestController
@RequestMapping("/admin/user")
public class AdminUserController extends BaseController {

    @Resource
    private AdminUserService adminUserService;


    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取用户列表")
    @EasyLog(module = "获取用户列表", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/page")
    public Result<IPage<AdminUser>> page(@Validated AdminUserQuery query) {
        return Result.success(adminUserService.selectPage(query));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取用户详情")
    @EasyLog(module = "获取用户详情", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/detail/{id}")
    public Result<AdminUser> detail(@PathVariable Long id) {
        return Result.success(adminUserService.detail(id));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取当前用户信息")
    @EasyLog(module = "获取当前用户信息", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/info")
    public Result<AdminUserInfo> info() {
        return Result.success(adminUserService.getInfo());
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "创建用户")
    @EasyLog(module = "创建用户", operateType = OperateTypeEnum.INSERT)
    @PostMapping(value = "/create")
    public Result create(@Validated @RequestBody AdminUserCreateDTO dto) {
        return Result.r(adminUserService.create(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "编辑用户")
    @EasyLog(module = "编辑用户", operateType = OperateTypeEnum.UPDATE)
    @PostMapping(value = "/update")
    public Result update(@Validated @RequestBody AdminUserUpdateDTO dto) {
        return Result.r(adminUserService.updateById(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "修改密码")
    @EasyLog(module = "修改密码", operateType = OperateTypeEnum.UPDATE)
    @PostMapping(value = "/editPassword")
    public Result updatePassword(@Validated @RequestBody EditPasswordDTO dto) {
        return Result.r(adminUserService.editPassword(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "重置密码")
    @EasyLog(module = "重置密码", operateType = OperateTypeEnum.UPDATE)
    @PostMapping(value = "/resetPassword")
    public Result resetPassword(@Validated @RequestBody ResetPasswordDTO dto) {
        return Result.r(adminUserService.resetPassword(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "删除用户")
    @EasyLog(module = "删除用户", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.r(adminUserService.deleteById(id));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "批量删除用户")
    @EasyLog(module = "批量删除用户", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/batchDel/{ids}")
    public Result batchDel(@PathVariable List<Long> ids) {
        return Result.r(adminUserService.deleteBatchByIds(ids));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "导入用户")
    @EasyLog(module = "导入用户", operateType = OperateTypeEnum.IMPORT)
    @PostMapping("/import")
    public Result<ImportVO> importExcel(UploadDTO dto) {
        Assert.notNull(dto.getFile(), "文件不能为空");
        try {
            List<AdminUser> list = EasyExcel.read(dto.getFile().getInputStream())
                    .head(AdminUser.class)
                    .excelType(FileUtil.getExcelType(dto.getFile()))
                    .sheet()
                    .doReadSync();
            List<ImportExcelError> errors = new ArrayList<>();
            List<AdminUser> errorList = new ArrayList<>();
            // 导入Excel处理
            adminUserService.importExcel(list, errorList, errors);
            String filePath = "";
            if (!errorList.isEmpty()) {
                // 将错误数据写到Excel文件
                filePath = FileUtil.getFullPath(easyFile.getFilePath(), "用户导入错误信息");
                EasyExcel.write(filePath).head(AdminUser.class)
                        .sheet().registerWriteHandler(new ImportErrorCellWriteHandler(errors))
                        .doWrite(errorList);
                filePath = FileUtil.getMapPath(filePath, easyFile.getFilePath(), easyFile.getFileMapPath());
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
    @ApiOperation(value = "导出用户")
    @EasyLog(module = "导出用户", operateType = OperateTypeEnum.EXPORT)
    @PostMapping("/export")
    public void exportExcel(@Validated @RequestBody AdminUserQuery query) {
        String filePath = FileUtil.getFullPath(easyFile.getExcelPath(), "用户");
        query.setPageNum(1);
        query.setPageSize(maxLimit);
        ExcelWriter build = EasyExcel.write(filePath, AdminUser.class)
                .excludeColumnFieldNames(Collections.singletonList("password"))
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet("用户").build();
        while (true) {
            IPage<AdminUser> page = adminUserService.selectPage(query);
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
    @ApiOperation(value = "下载用户导入模板")
    @EasyLog(module = "下载用户导入模板", operateType = OperateTypeEnum.DOWNLOAD)
    @PostMapping("/download")
    public void downloadTemplate(HttpServletResponse response) {
        String filePath = FileUtil.getFullPath(easyFile.getExcelPath(), "用户导入模板");
        EasyExcel.write(filePath, AdminUser.class)
                .excludeColumnFieldNames(Collections.singletonList("createTime"))
                .sheet("用户导入模板")
                .doWrite(new ArrayList<>());
        try {
            FileUtil.downloadAndDelete(filePath, response);
        } catch (IOException e) {
            log.error("下载用户导入模板失败 e -> ", e);
            throw new FileException("下载用户导入模板失败");
        }
    }
}
