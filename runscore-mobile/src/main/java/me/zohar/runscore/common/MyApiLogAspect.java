package me.zohar.runscore.common;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @class MyApiLogAspect
 * @classdesc 实现ApiLog的功能，即打印请求日志和响应日志
 * @author chh
 * @date 2020/8/26  15:07
 * @version 1.0.0
 * @see
 * @since
 */
@Aspect
@Component
public class MyApiLogAspect {
    private static final Logger log = LoggerFactory.getLogger(MyApiLogAspect.class);

    /**
     * 记录业务请求的时间
     */
    private long req;

    private String reqTime;

    /**
     * @param: void
     * @return: void
     * @desc: 定义空方法用于切点表达式
     * @see
     * @since
     */
    @Pointcut("@annotation(me.zohar.runscore.common.ApiLog)")
    public void pointcut(){
        //do nothing just for filtering
    }

    /**
     * @param: [joinPoint]
     * @return: void
     * @desc: 在进入controller之前拦截并打印请求报文日志
     * @see
     * @since
     */
    @Before("pointcut()")
    public void printRequestDatagram(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = getIpAddress(request);
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        req = System.currentTimeMillis();
        reqTime = dateFormat.format (new Date(req));
        Object[] args = joinPoint.getArgs ();
        log.info ("\n==> 拦截到请求\n"
                + "==> 请求者IP：" + ip + "\n"
                + "==> 请求时间：" + reqTime + "\n"
                + "==> 请求接口：" + request.getMethod() + " " + request.getRequestURL() + "\n"
                + "==> 请求方法：" + method.getName() + "\n"
                + "==> 参数内容："+ Arrays.toString(args));
    }

    /**
     * @param: [joinPoint]
     * @return: java.lang.Object
     * @desc: 返回信息后，打印应答报文的日志
     * @see
     * @since
     */
    @Around("pointcut()")
    public Object printResponseDatagram(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        result = joinPoint.proceed ();
        String stuString = JSONObject.toJSONString(result);

        long respTime = System.currentTimeMillis() - req;
        String d = String.valueOf(respTime);
        log.info ( "\n<== 响应请求\n"
                + "<== 请求时间：" + reqTime + "\n"
                + "<== 请求耗时：" + Double.parseDouble(d)/1000 + "s\n"
                + "<== 应答内容：" + stuString );
        return result;
    }

    /**
     * @param: [request]
     * @return: java.lang.String
     * @desc: 获取IP地址
     * @see
     * @since
     */
    private String getIpAddress(HttpServletRequest request){
        final String UNKNOWN = "unknown";
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
