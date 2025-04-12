package com.example.multitenant.common.resolvers.impl;

import org.hibernate.query.SortDirection;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.multitenant.common.resolvers.contract.HandlePage;

@Component
public class HandlePageResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(HandlePage.class) &&
        parameter.getParameterType().equals(Integer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var annotation = parameter.getParameterAnnotation(HandlePage.class);
        var value = webRequest.getParameter("page");

        try {
            int page = Integer.parseInt(value);
            return (page < 1) ? 1 : page;
        } catch (Exception e) {
            return 1;
        }
    }
}
