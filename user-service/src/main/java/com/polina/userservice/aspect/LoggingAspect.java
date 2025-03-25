package com.polina.userservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.polina..controller..*(..))")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        HttpServletResponse response = attributes != null ? attributes.getResponse() : null;

        String ipAddress = request != null ? request.getRemoteAddr() : "UNKNOWN";
        String requestedUrl = request != null ? request.getRequestURI() : "UNKNOWN";

        long startTime = System.currentTimeMillis();
        logger.info("[REQUEST] IP: {} | URL: {} | Method: {} | Params: {}",
                 ipAddress, requestedUrl, joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception ex) {
            logger.error("[ERROR] URL: {} | Exception: {}", requestedUrl, ex.getMessage(), ex);
            throw ex;
        }

        long duration = System.currentTimeMillis() - startTime;
        int statusCode = response != null ? response.getStatus() : 0;

        logger.info("[RESPONSE] URL: {} | Status: {} | Duration: {}ms | Response: {}",
                requestedUrl, statusCode, duration, result);

        return result;
    }

}
