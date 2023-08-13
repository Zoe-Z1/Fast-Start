package com.fast.start.admin.loginLog.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.start.admin.loginLog.entity.LoginLog;
import com.fast.start.admin.loginLog.entity.LoginLogCreateDTO;
import com.fast.start.admin.loginLog.entity.LoginLogQuery;
import com.fast.start.admin.loginLog.mapper.LoginLogMapper;
import com.fast.start.admin.loginLog.service.ILoginLogService;
import com.fast.start.common.base.BaseEntity;
import com.fast.start.utils.JsonUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
* @author zoe
* @date 2023/08/02
* @description 登录日志 服务实现类
*/
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

    @Override
    public IPage<LoginLog> selectPage(LoginLogQuery query) {
        Page<LoginLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        return lambdaQuery()
                .like(StrUtil.isNotEmpty(query.getIp()), LoginLog::getIp, query.getIp())
                .like(StrUtil.isNotEmpty(query.getUsername()), LoginLog::getUsername, query.getUsername())
                .like(StrUtil.isNotEmpty(query.getBrowser()), LoginLog::getBrowser, query.getBrowser())
                .like(StrUtil.isNotEmpty(query.getOs()), LoginLog::getOs, query.getOs())
                .like(StrUtil.isNotEmpty(query.getPro()), LoginLog::getPro, query.getPro())
                .like(StrUtil.isNotEmpty(query.getProCode()), LoginLog::getProCode, query.getProCode())
                .like(StrUtil.isNotEmpty(query.getCity()), LoginLog::getCity, query.getCity())
                .like(StrUtil.isNotEmpty(query.getCityCode()), LoginLog::getCityCode, query.getCityCode())
                .like(StrUtil.isNotEmpty(query.getAddr()), LoginLog::getAddr, query.getAddr())
                .eq(StrUtil.isNotEmpty(query.getStatus()), LoginLog::getStatus, query.getStatus())
                .between(Objects.nonNull(query.getStartTime()) && Objects.nonNull(query.getEndTime()),
                        BaseEntity::getCreateTime, query.getStartTime(), query.getEndTime())
                .orderByDesc(BaseEntity::getCreateTime)
                .page(page);
    }

    @Override
    public LoginLog detail(Long id) {
        return getById(id);
    }

    @Async("LogThreadPoolTaskExecutor")
    @Override
    public void asyncCreate(@Validated LoginLogCreateDTO dto) {
        LoginLog loginLog = this.getLoginLog(dto.getUserAgent(), dto.getIp());
        loginLog.setUsername(dto.getUsername());
        loginLog.setStatus(dto.getStatus());
        loginLog.setRemarks(dto.getRemarks());
        loginLog.setCreateBy(dto.getUserId());
        save(loginLog);
    }

    @Override
    public LoginLog getLoginLog(String userAgent, String ip) {
        LoginLog log = new LoginLog();
        Map<String, Object> map = new HashMap<>();
        map.put("json", true);
        map.put("ip", ip);
        String url = "https://whois.pconline.com.cn/ipJson.jsp";
        String result = HttpUtil.get(url, map);
        if (StrUtil.isEmpty(result)) {
            return log;
        }
        try{
            log = JsonUtil.toBean(result, LoginLog.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        UserAgent ua = UserAgentUtil.parse(userAgent);
        log.setBrowser(ua.getBrowser().getName());
        log.setOs(ua.getOs().getName());
        log.setEngine(ua.getEngine().getName());
        return log;
    }

    @Override
    public Boolean deleteById(Long id) {
        return removeById(id);
    }

    @Override
    public Boolean deleteBatchByIds(List<Long> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    public Boolean clear() {
        QueryWrapper<LoginLog> queryWrapper = new QueryWrapper<>();
        return remove(queryWrapper);
    }

}
