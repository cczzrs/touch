package org.cczzrs.core.log;

import java.util.Arrays;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.util.GenericSignature.ClassSignature;
import org.cczzrs.core.utils.MyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.log4j.Log4j2;

/**
 * @fileName WebLogAspect.java
 * @author CCZZRS
 * @date 2020-10-31 17:25:33
 * @description 注解（@WebLog）的 AOP
 *  */
@Order(1)
@Aspect
@Component
@Log4j2
public class WebLogAspect {

    @Value("${spring.profiles.active}")
    private String ENV;

    @Pointcut("@annotation(com.hbtz.zhzf.core.log.WebLog)")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("================================= begin =================================");
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();// 执行切入点 或 者进入下一个AOP
        // 获取 @WebLog 注解的描述信息
        WebLog webLog = getAnnotation(proceedingJoinPoint);
        if(webLog.isPrintResult() || "dev".equals(ENV)){
            log.info("Result Args    M: {}", JSONObject.toJSONString(result));
        }
        log.info("Time-Consuming M: {} ms", System.currentTimeMillis() - startTime);
        log.info("=================================  end  =================================");
        return result;
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // log.info("================================= Before begin =================================");
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 获取 @WebLog 注解的描述信息
        WebLog webLog = getAnnotation(joinPoint);

        // 打印请求相关参数
        // 打印请求 IP Method URL
        log.info("IP Method URL  W: [{}] {} => {}", MyUtil.getIpAddress(request), request.getMethod(), request.getRequestURL().toString());
        // 打印描述信息
        log.info("Description    W: {}", webLog.description());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   W: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求入参
        log.info("Request Args   W: {}", joinPoint.getArgs()==null?"[]":JSON.toJSONString(Arrays.stream(joinPoint.getArgs()).map(a -> a instanceof ServletRequest?"servletRequest":(a instanceof ServletResponse?"servletResponse":String.valueOf(a))).toArray()));
    }

    @AfterReturning("webLog()")
    public void doAfterReturning() throws Throwable {
        // log.info("================================= AfterReturning end =================================");
    }

    @After("webLog()")
    public void doAfter() throws Throwable {
        // log.info(" ================================= After end =================================");
    }

    /**
     * @fileName WebLogAspect.java
     * @author CCZZRS
     * @date 2020-10-31 14:20:54
     * @description 获取切面注解 - WebLog
     * @param joinPoint 切点
     * @return 注解 - WebLog
     * @throws Exception
     */
    public WebLog getAnnotation(JoinPoint joinPoint) throws Exception {
        if (joinPoint.getSignature() instanceof MethodSignature) {// 判断注解是否是在 方法上
            MethodSignature msig = (MethodSignature) joinPoint.getSignature();
            return joinPoint.getTarget().getClass().getMethod(msig.getName(), msig.getParameterTypes()).getAnnotation(WebLog.class);
        }else if (joinPoint.getSignature() instanceof ClassSignature) {// 注解在 class上
            // throw new IllegalArgumentException("<<@WebLog>>该注解只能用于方法");
            // MethodSignature msig = (MethodSignature) joinPoint.getSignature();
            return joinPoint.getTarget().getClass().getAnnotation(WebLog.class);
        }
        throw new IllegalArgumentException("<<@WebLog>>该注解只能用于方法和ClassSignature上");
    }

    /**
     * 获取切面注解的描述
     * @param joinPoint 切点
     * @return 描述信息
     * @throws Exception
     */
    // public String getAspectLogDescription(JoinPoint joinPoint)
    //         throws Exception {
    //     String targetName = joinPoint.getTarget().getClass().getName();
    //     String methodName = joinPoint.getSignature().getName();
    //     Object[] arguments = joinPoint.getArgs();
    //     Class targetClass = Class.forName(targetName);
    //     Method[] methods = targetClass.getMethods();
    //     StringBuilder description = new StringBuilder("");
    //     for (Method method : methods) {
    //         if (method.getName().equals(methodName)) {
    //             Class[] clazzs = method.getParameterTypes();
    //             if (clazzs.length == arguments.length) {
    //                 description.append(method.getAnnotation(WebLog.class).description());
    //                 break;
    //             }
    //         }
    //     }
    //     return description.toString();
    // }
}
