package com.example.toolsaspect.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 通用日志切面
 * 功能特性
 *      记录请求参数（controller和service）
 *      每个请求都有唯一请求id
 *      打印返回值
 *      打印异常
 * @author 石佳
 * @since 2021/7/9
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    @Pointcut("execution(* com.example.toolsaspect.controller.*.*(..))")
    public void controllerLog() {

    }

    @Pointcut("execution(* com.example.toolsaspect.service.*.*(..))")
    public void serviceLog() {

    }

    /**
     * 方法请求参数
     */
    @Before("controllerLog()||serviceLog()")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) return;

        Object requestIdO = request.getAttribute("requestId");
        if(requestIdO==null){
            request.setAttribute("requestId",UUID.randomUUID().toString());
        }

        // 记录下请求内容
        log.info("requestIp:{},requestId:{},url:{},http_method:{},args:{},class_method:{}.{}",
                request.getRemoteAddr(),
                request.getAttribute("requestId"),
                request.getRequestURI(),
                request.getMethod(),
                joinPoint.getArgs(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if(servletRequestAttributes==null){
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    @AfterReturning(returning = "returnOb", pointcut = "controllerLog() ")
    public void doAfterReturning(JoinPoint joinPoint, Object returnOb) {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) return;
        log.info("requestId:{},result:{}",request.getAttribute("requestId"),returnOb);
    }

    @AfterThrowing(pointcut = "controllerLog() ", throwing = "ex")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) return;
        log.info("requestId:{},errorMsg:{}",request.getAttribute("requestId"),ex);
    }
}
