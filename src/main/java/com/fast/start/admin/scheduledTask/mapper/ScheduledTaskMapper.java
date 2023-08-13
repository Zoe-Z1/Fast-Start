package com.fast.start.admin.scheduledTask.mapper;

import com.fast.start.admin.scheduledTask.entity.ScheduledTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zoe
* @date 2023/08/04
* @description 定时任务 Mapper接口
*/
@Mapper
public interface ScheduledTaskMapper extends BaseMapper<ScheduledTask> {

}
