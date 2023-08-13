package com.fast.start.admin.taskLog.mapper;

import com.fast.start.admin.taskLog.entity.TaskLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zoe
* @date 2023/08/06
* @description 调度日志 Mapper接口
*/
@Mapper
public interface TaskLogMapper extends BaseMapper<TaskLog> {

}