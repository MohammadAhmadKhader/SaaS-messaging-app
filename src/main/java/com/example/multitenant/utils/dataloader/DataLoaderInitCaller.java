package com.example.multitenant.utils.dataloader;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import jakarta.annotation.PostConstruct;

@Component
public class DataLoaderInitCaller {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired 
    DataLoader dataLoader;

    @PostConstruct
    public void Init() {
        try {
            dataLoader.loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}