package com.example.demo.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FiltersHandler {
    public static Map<String, String> parseFilters(List<String> filters) {
        var map = new HashMap<String, String>();
        for(String s: filters) {
            String[] filterPars = s.split(":");
            if(filterPars.length == 2) {
                String field = filterPars[0];
                String value = filterPars[1];

                map.put(field, value);
            }
        }

        return map;
    }
}
