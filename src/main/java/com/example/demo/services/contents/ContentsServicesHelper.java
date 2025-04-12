package com.example.demo.services.contents;

import org.springframework.stereotype.Component;

import com.example.demo.models.Content;
import com.example.demo.utils.ServicesHelper;

@Component
public class ContentsServicesHelper extends ServicesHelper<Content> {
    public ContentsServicesHelper() {
        super(Content.class);
    }
}
