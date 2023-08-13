package com.fast.start.common.noRepeatSubmit;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.start.exception.BusinessException;
import com.fast.start.common.redis.FastRedisManager;
import com.fast.start.common.redis.RedisKeyEnum;
import com.fast.start.exception.enums.SystemErrorEnum;
import com.fast.start.utils.IpUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zoe
 * @describe 自定义防重复提交注解实现
 * @date 2023/7/22
 */
@Aspect
@Component
public class FastNoRepeatSubmitAspect {

    @Resource
    private FastRedisManager fastRedisManager;

    @Resource
    private com.fast.start.common.properties.FastNoRepeatSubmit fastNoRepeatSubmit;

    @Resource
    private HttpServletRequest request;

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.fast.start.common.noRepeatSubmit.FastNoRepeatSubmit)")
    public void noRepeatSubmitPointCut() {
    }

    /**
     * 配置织入点
     */
    @Pointcut("execution(public * com.fast.start.*.*.controller..*(..))")
    public void noRepeatSubmitClassPointCut() {
    }

    /**
     * 前置通知
     */
    @Before("noRepeatSubmitPointCut() || noRepeatSubmitClassPointCut()")
    public void before(JoinPoint joinPoint) {
        // 判断注解是否开启
        if (!this.fastNoRepeatSubmit.getEnable()){
            return;
        }
        // 获取注解
        FastNoRepeatSubmit fastNoRepeatSubmit = getNoRepeatSubmit(joinPoint);
        if (fastNoRepeatSubmit == null){
            return;
        }
        // 获取key
        String key = getKey(joinPoint);
        // 重复提交判定时间
        long timeout = fastNoRepeatSubmit.timeout();
        if (timeout == 0){
            timeout = this.fastNoRepeatSubmit.getTimeout();
        }
        String message = fastNoRepeatSubmit.message();
        // 获取锁
        Boolean lock = fastRedisManager.lock(key, "", timeout);
        // 锁未获取成功则代表重复提交
        if (!lock) {
            throw new BusinessException(SystemErrorEnum.FORBID_REPEAT_SUBMIT.getCode(), message);
        }
    }

    /**
     * 获取key
     * @param joinPoint
     * @return
     */
    private String getKey(JoinPoint joinPoint){
        // 获取当前调用的方法路径
        String method = getMethod(joinPoint);
        String ip = IpUtil.getIp(request);
        if (StrUtil.isEmpty(ip) || NetUtil.isUnknown(ip)) {
            throw new BusinessException(SystemErrorEnum.IP_UNKNOWN);
        }
        String key = RedisKeyEnum.NO_REPEAT_SUBMIT.getKey(ip + ":" + method);
        return key;
    }

    private static String getIp() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
        }
        return ip;
    }

    /**
     * 获取调用方法路径
     * @param joinPoint
     * @return
     */
    private String getMethod(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
    }

    /**
     * 获取注解
     */
    private FastNoRepeatSubmit getNoRepeatSubmit(JoinPoint joinPoint) {
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Method method = sign.getMethod();
        FastNoRepeatSubmit fastNoRepeatSubmit = method.getAnnotation(FastNoRepeatSubmit.class);
        if (fastNoRepeatSubmit == null) {
            fastNoRepeatSubmit = joinPoint.getTarget().getClass().getAnnotation(FastNoRepeatSubmit.class);
        }
        return fastNoRepeatSubmit;
    }
}
