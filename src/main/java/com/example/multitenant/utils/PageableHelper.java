package com.example.multitenant.utils;

import org.springframework.data.domain.*;

public class PageableHelper {
    public static Pageable HandleSortWithPagination(String defaultSortBy, String defaultSortDir, String sortBy, String sortDir, Integer page, Integer size) {
        if(sortDir == null || 
            !sortDir.equalsIgnoreCase("DESC") ||
            !sortDir.equalsIgnoreCase("ASC")) {
            sortDir = defaultSortDir;
        }
        
        if(sortBy == null) {
            sortBy = defaultSortBy;
        }

        var sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        return pageable;
    }

    public static Pageable HandleSortWithPagination(String sortBy, String sortDir, Integer page, Integer size) {
        var sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        return pageable;
    }
}
