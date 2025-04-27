package com.example.multitenant.common.annotations.impl;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.*;
import org.springframework.stereotype.Component;

import com.example.multitenant.common.annotations.contract.LogMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogMethodImpl {

    @Pointcut("@annotation(com.example.multitenant.common.annotations.contract.LogMethod)")
    public void loggableMethod() {}

    @Around("loggableMethod()")
    public Object logMethodResult(ProceedingJoinPoint joinPoint) throws Throwable {
        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var methodName = method.getName();
        var className = method.getDeclaringClass().getSimpleName();
        var arguments = joinPoint.getArgs();
        var parameters = method.getParameters();

        var argsLog = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            argsLog.append(parameters[i].getName()).append(": ").append(arguments[i]).append(" ");
        }

        log.info("executing method: {} of class: {} with arguments: {}", methodName, className, argsLog);
        var result = joinPoint.proceed();
        log.info("method {} from class {} was executed with result: {}", methodName, className, result.toString());

        return result;
    }
}