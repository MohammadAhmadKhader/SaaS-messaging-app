package com.example.multitenant.utils.dataloader;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

@Component
public class DataLoaderInitCaller {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired 
    DataLoader dataLoader;

    public void init() {
        try {
            dataLoader.loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}