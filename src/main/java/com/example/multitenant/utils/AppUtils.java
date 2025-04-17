package com.example.multitenant.utils;

import java.beans.FeatureDescriptor;
import java.util.Arrays;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AppUtils {
    public static <TModel> void copyNonNullProperties(TModel source, TModel target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    // TODO: refactor this function
    private static String[] getNullPropertyNames(Object source) {
        var src = new BeanWrapperImpl(source);

        return Arrays.stream(src.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
    
    public static Integer getTenantId() {
        var req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        var tenantIdStr = req.getHeader("X-Tenant-ID");
        
        return Integer.parseInt(tenantIdStr);
    }
}
