package com.example.demo.common.resolvers.impl;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.demo.common.resolvers.contract.HandleSize;

@Component
public class HandleSizeResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(HandleSize.class) &&
        parameter.getParameterType().equals(Integer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var annotation = parameter.getParameterAnnotation(HandleSize.class);
        var value = webRequest.getParameter("size");
        int defaultSize = annotation.defaultSize();
        int minSize = annotation.minSize();

        try {
            int size = Integer.parseInt(value);
            return (size < minSize) ? defaultSize : size;
        } catch (Exception e) {
            return minSize;
        }
    }
}
