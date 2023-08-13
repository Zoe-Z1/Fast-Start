package com.fast.start.admin.server.controller;

import cn.hutool.core.date.DateUtil;
import com.fast.start.admin.operationLog.enums.OperateTypeEnum;
import com.fast.start.admin.server.entity.*;
import com.fast.start.admin.server.service.ServerService;
import com.fast.start.common.base.Result;
import com.fast.start.common.log.FastLog;
import com.fast.start.utils.ServerUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author zoe
 * @date 2023/8/12
 * @description 服务器监控接口
 */
@Slf4j
@Api(tags = "服务器监控接口")
@RestController
@RequestMapping("/admin/server")
public class ServerController {

    @Resource
    private ServerService serverService;

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取服务器详情")
    @FastLog(module = "获取服务器详情", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/detail")
    public Result<ServerVO> detail() {
        return Result.success(serverService.getServerInfo());
    }

    public static void main(String[] args) {
        long start = DateUtil.current();

        Cpu cpu = ServerUtil.getMyCpu();
        System.out.println("cpu = " + cpu);
        Os os = ServerUtil.getMyOs();
        System.out.println("os = " + os);
        Memory memory = ServerUtil.getMyMemory();
        System.out.println("memory = " + memory);
        Jvm jvm = ServerUtil.getMyJvm();
        System.out.println("jvm = " + jvm);
        Disk disk = ServerUtil.getMyDisk();
        System.out.println("disk = " + disk);
        Network network = ServerUtil.getMyNetwork();
        System.out.println("network = " + network);

        long end = DateUtil.current();
        long time = end - start;
        System.out.println("time = " + time);
    }

}
