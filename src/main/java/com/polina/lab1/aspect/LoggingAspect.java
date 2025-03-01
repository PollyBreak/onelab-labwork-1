package com.polina.lab1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.polina.lab1.repository.*.*(..))")
    public void beforeAdvice() {
        System.out.println("[AOP] This advice executes before method");
    }

    @After("execution(* com.polina.lab1.repository.*.*(..))")
    public void afterAdvice() {
        System.out.println("[AOP] This advice executes after method");
    }

    @AfterReturning(value = "execution(* com.polina.lab1.repository.*.*(..))", returning = "result")
    public void afterReturningAdvice(Object result) {
        System.out.println("[AOP] This advice executes successfully after method returning. Returned: " + result);
    }

    @AfterThrowing(value = "execution(* com.polina.lab1.repository.*.*(..))", throwing = "exception")
    public void afterThrowingAdvice(Exception exception) {
        System.out.println("[AOP] Exception occurred: " + exception.getMessage());
    }

    @Around("execution(* com.polina.lab1.repository.*.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        System.out.println("[AOP] Method called: " + joinPoint.getSignature());
        System.out.println("[AOP] Arguments: " + Arrays.toString(joinPoint.getArgs()));
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("[AOP] Execution completed: " + joinPoint.getSignature() +
                " in " + (endTime - startTime) + "ms");
        return result;
    }

}
