package com.example.multitenant.services.contents;

import org.springframework.stereotype.Component;

import com.example.multitenant.models.Content;
import com.example.multitenant.utils.ServicesHelper;

@Component
public class ContentsServicesHelper extends ServicesHelper<Content> {
    public ContentsServicesHelper() {
        super(Content.class);
    }
}
